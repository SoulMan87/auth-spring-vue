package com.soulrebel.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${application.security.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf ().disable ()
                .cors ().and ()
                .authorizeHttpRequests ()
                .antMatchers ("/**")
                .permitAll ();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration ();
        configuration.setAllowedOrigins (allowedOrigins);
        configuration.setAllowedMethods (List.of ("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTION"));
        configuration.setAllowCredentials (true);
        configuration.setAllowedHeaders (List.of ("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource ();
        source.registerCorsConfiguration ("/**", configuration);

        return source;
    }
}
