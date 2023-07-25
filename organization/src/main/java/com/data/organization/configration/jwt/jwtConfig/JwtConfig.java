package com.data.organization.configration.jwt.jwtConfig;

import javax.crypto.SecretKey;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtConfig {

    @NotBlank
    private String secretKey;
    @NotBlank
    private String tokenPrefix;
    @NotNull
    private Integer tokenExpirationAfterDays;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

    @Bean
    SecretKey secretKey() {
        return Keys.hmacShaKeyFor(getSecretKey().getBytes());
    }
}
