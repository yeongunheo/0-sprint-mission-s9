package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.IntStream;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
        .logout(AbstractHttpConfigurer::disable); // 로그아웃 관련 필터 제외

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
}
