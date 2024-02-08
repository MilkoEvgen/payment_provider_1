package com.milko.payment_provider.service;

import com.milko.payment_provider.model.Transaction;
import com.milko.payment_provider.model.WebhookInfo;
import com.milko.payment_provider.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final WebhookRepository webhookRepository;

    public Mono<Void> sendWebHook(Transaction body) {
        log.info("IN WebhookService sendWebHook, transaction id = {}", body.getId());

        WebClient webClient = WebClient.builder()
                .baseUrl(body.getNotificationUrl())
                .build();

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Transaction.class)
                .exchangeToMono(clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .defaultIfEmpty("No content")
                            .flatMap(response -> {
                                WebhookInfo webhookInfo = WebhookInfo.builder()
                                        .url(body.getNotificationUrl())
                                        .dateTime(LocalDateTime.now())
                                        .statusCode(clientResponse.statusCode().value())
                                        .response(clientResponse.statusCode().isError() ? "Error response received" : response)
                                        .build();
                                return webhookRepository.save(webhookInfo).then();
                            });
                })
                .onErrorResume(error -> {
                    WebhookInfo webhookInfo = WebhookInfo.builder()
                            .url(body.getNotificationUrl())
                            .dateTime(LocalDateTime.now())
                            .statusCode(0)
                            .response(error.getMessage())
                            .build();
                    return webhookRepository.save(webhookInfo).then(Mono.error(error));
                })
                .retryWhen(Retry.fixedDelay(4, Duration.ofSeconds(5)))
                .onErrorResume(throwable -> Mono.empty());
    }

}
