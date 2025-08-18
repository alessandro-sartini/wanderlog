package com.travel.wanderlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("dev") // <— ATTIVA SOLO IN DEV
public class SecurityDevConfig {

    @Bean
    SecurityFilterChain devSecurity(HttpSecurity http) throws Exception {
        http
                // API stateless: niente sessione e niente CSRF (useremo JWT più avanti)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // se vuoi lasciare aperta anche la doc:
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // apri tutte le API per ora
                        .requestMatchers("/api/**").permitAll()
                        // tutto il resto aperto (statici/health ecc.)
                        .anyRequest().permitAll())
                // opzionale: Basic per test manuali, ma non serve se già permitAll
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
