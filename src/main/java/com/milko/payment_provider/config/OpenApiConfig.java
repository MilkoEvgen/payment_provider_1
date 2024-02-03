package com.milko.payment_provider.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Payment Provider Api",
                description = "This service will provide us with the ability to create real transactions (top up, withdrawal), send webhooks and check the merchant balance",
                version = "1.0.0",
                contact = @Contact(
                        name = "Milko Eugene"
                )
        )
)
public class OpenApiConfig {

}