package com.milko.payment_provider.mapper;

import com.milko.payment_provider.dto.CardDataDto;
import com.milko.payment_provider.model.CardData;
import org.springframework.stereotype.Component;

@Component
public class CardDataMapper {
    public CardData map(CardDataDto cardDataDto){
        return CardData.builder()
                .cardNumber(cardDataDto.getCardNumber())
                .cvv(cardDataDto.getCvv())
                .expDate(cardDataDto.getExpDate())
                .build();
    }

    public CardDataDto map(CardData cardData){
        return CardDataDto.builder()
                .id(cardData.getId())
                .cardNumber(cardData.getCardNumber())
                .cvv(cardData.getCvv())
                .expDate(cardData.getExpDate())
                .build();
    }
}
