package com.github.tecnoguard.infrastructure.config;

import com.github.tecnoguard.infrastructure.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl service;

    public SecurityConfig(UserDetailsServiceImpl service) {
        this.service = service;
    }

    /*@Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        var user = //service.loadUserByUsername("admin");
                User
                        .withUsername("admin")
                        .password("{noop}1234")
                        .roles("ADMIN")
                        .build();
        return new InMemoryUserDetailsManager(user);
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/auth/**",
                                        "/h2-console/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService service, PasswordEncoder encoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

}
