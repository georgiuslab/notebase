package chemilabi.trade.notebase_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${server.uri}")
    private String serverURL;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "keycloakOAuth";

        String authUrl = issuerUri + "/protocol/openid-connect/auth";
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl(authUrl)
                .tokenUrl(tokenUrl)
                .scopes(new Scopes()
                        .addString("openid", "OpenID Connect")
                        .addString("profile", "User Profile")
                        .addString("email", "User Email"));

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(serverURL).description("API Gateway")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows().authorizationCode(authorizationCodeFlow))
                        )
                );
    }
}