package com.aldenleon.treeter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/list-tree-view",
                                "/error",
                                "/access-denied",
                                "/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedPage("/access-denied"))
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
