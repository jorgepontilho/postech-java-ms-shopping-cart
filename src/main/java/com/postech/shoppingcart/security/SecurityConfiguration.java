package com.postech.shoppingcart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests( authorizeConfig -> {
                    authorizeConfig.requestMatchers("/carts/**").permitAll();
                    authorizeConfig.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    authorizeConfig.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults());
        return http.build();
    }
}

