package com.milko.payment_provider.service;

import com.milko.payment_provider.repository.MerchantRepository;
import com.milko.payment_provider.security.MerchantDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MerchantService implements ReactiveUserDetailsService {
    private final MerchantRepository merchantRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return merchantRepository.findByUsername(username)
                .map(MerchantDetails::new);
    }
}
