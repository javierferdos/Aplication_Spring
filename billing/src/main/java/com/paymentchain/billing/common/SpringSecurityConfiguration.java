/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.billing.common;

import ch.qos.logback.core.CoreConstants;
import java.util.Arrays;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 * @author javie
 */@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    private static final String[] NO_AUTH_LIST = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-ui/index.html",
        "/swagger-resources/**",
        "/webjars/**",
        "/h2-console/**",
        "/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()

            // activar CORS para que Spring Security utilice el CorsConfigurationSource
            .cors().and()

            // permitir H2 console en dev
            .headers().frameOptions().disable().and()

            .authorizeHttpRequests(authz -> authz
                .requestMatchers(NO_AUTH_LIST).permitAll()
                .requestMatchers(HttpMethod.POST, "/billing/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                // si quieres que GET /billing requiera auth:
                // .requestMatchers(HttpMethod.GET, "/billing/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .formLogin(withDefaults());

        return http.build();
    }

    // si mantienes SecurityAutoConfiguration excluido, crea usuarios aquí:
    @Bean
    public InMemoryUserDetailsManager users() {
        var admin = User.withUsername("admin")
                .password("{noop}qwerty")   // {noop} solo para desarrollo
                .roles("ASESOR")            // producirá ROLE_ASESOR
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    // CorsConfigurationSource (tuya, ya esta correcta) — se usará porque llamamos http.cors()
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cc = new CorsConfiguration();
        cc.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // o addAllowedOriginPattern("*") en dev
        cc.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cc.setAllowedHeaders(Arrays.asList("Origin","Accept","X-Requested-With","Content-Type","Authorization","Access-Control-Request-Method","Access-Control-Request-Headers"));
        cc.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin","Access-Control-Allow-Credentials","Authorization"));
        cc.setAllowCredentials(Boolean.TRUE);
        cc.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cc);
        return source;
    }
}
