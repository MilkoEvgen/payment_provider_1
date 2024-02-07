package com.milko.payment_provider.service;

import com.milko.payment_provider.repository.MerchantRepository;
import com.milko.payment_provider.security.MerchantDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MerchantService implements ReactiveUserDetailsService {
    private final MerchantRepository merchantRepository;

    @Override
    public Mono<UserDetails> findByUsername(String uuid) {
        return merchantRepository.findById(uuid)
                .map(MerchantDetails::new);
    }
}
