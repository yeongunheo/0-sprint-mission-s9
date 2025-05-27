package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.JsonUsernamePasswordAuthenticationFilter;
import com.sprint.mission.discodeit.security.SecurityMatchers;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.IntStream;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      ObjectMapper objectMapper,
      AuthenticationManager authenticationManager
  ) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                SecurityMatchers.NON_API,
                SecurityMatchers.GET_CSRF_TOKEN,
                SecurityMatchers.SIGN_UP
            )
            .permitAll()
            .anyRequest().authenticated()
        )
        .logout(AbstractHttpConfigurer::disable) // 로그아웃 관련 필터 제외
        .addFilterAt(
            JsonUsernamePasswordAuthenticationFilter.createDefault(
                objectMapper,
                authenticationManager
            ),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }

  @Bean
  public String debugFilterChain(SecurityFilterChain chain) {
    log.debug("Debug Filter Chain...");
    int filterSize = chain.getFilters().size();
    IntStream.range(0, filterSize)
        .forEach(index -> {
          log.debug("[{}/{}] {}", index + 1, filterSize, chain.getFilters().get(index));
        });
    return "debugFilterChain";
  }

  @Bean
  public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
    return new ProviderManager(authenticationProviders);
  }
}
