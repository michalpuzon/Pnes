package org.ekipa.pnes.api.configs.security;

import lombok.RequiredArgsConstructor;
import org.ekipa.pnes.api.configs.security.fillters.JwtFillter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends MySecurityConfig {
    private final JwtFillter jwtFillter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static {
        setWhitelist(Arrays.asList(
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "**/swagger-resources/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui/"
        ));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        setupEndpoints(http).authorizeRequests()
                .anyRequest().authenticated().and()
                .csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFillter, UsernamePasswordAuthenticationFilter.class);
    }

}