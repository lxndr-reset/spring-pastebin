package com.pastebin.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                version = "1.0",
                title = "Spring Pastebin",
                contact = @Contact(
                        name = "lxndr-reset",
                        email = "lxndr.nonstop@gmail.com",
                        url = "https://linktr.ee/lxndr_reset"
                ),

                description = "Feel free to use :D"
        ),
        security = {
                @SecurityRequirement(
                        name = "Registered"
                )
        },
        servers = {
                @Server(
                        description = "Local",
                        url = "https://localhost:8080"
                ),
                @Server(
                        description = "Prod",
                        url = "https://NOTVALIDURL.notvalidurl"
                )
        }
)
public class OpenAPIConfig {
}
