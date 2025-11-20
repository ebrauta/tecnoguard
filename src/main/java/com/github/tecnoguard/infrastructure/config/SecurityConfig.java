package com.github.tecnoguard.infrastructure.config;

import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.infrastructure.security.JwtAuthenticationEntryPoint;
import com.github.tecnoguard.infrastructure.security.JwtFilter;
import com.github.tecnoguard.infrastructure.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/auth/login",
                                        "/h2-console/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "SUPERVISOR")
                                .requestMatchers(HttpMethod.PATCH, "/api/users/deactivate/**").hasAnyRole("ADMIN", "SUPERVISOR")
                                .requestMatchers(HttpMethod.PATCH, "/api/users/reactivate/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/users/password/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/workorders").hasAnyRole("ADMIN", "PLANNER", "OPERATOR")
                                .requestMatchers(HttpMethod.PATCH, "/api/workorders/assign/**").hasAnyRole("ADMIN", "PLANNER")
                                .requestMatchers(HttpMethod.PATCH, "/api/workorders/start/**").hasAnyRole("ADMIN", "TECHNICIAN")
                                .requestMatchers(HttpMethod.PATCH, "/api/workorders/complete/**").hasAnyRole("ADMIN", "TECHNICIAN", "SUPERVISOR")
                                .requestMatchers(HttpMethod.PATCH, "/api/workorders/cancel/**").hasAnyRole("ADMIN", "SUPERVISOR")
                                .requestMatchers(HttpMethod.GET, "/api/workorders/log/**").hasAnyRole("ADMIN", "PLANNER", "SUPERVISOR")
                                .requestMatchers(HttpMethod.POST, "/api/workorders/log/**").hasAnyRole("ADMIN", "PLANNER", "SUPERVISOR", "TECHNICIAN")
                                .requestMatchers(HttpMethod.GET, "/api/logs").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
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
