package de.fhg.iese.oneviewmed.backend.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@AllArgsConstructor
public class WebMvcConfiguration {
  private final CorsProperties corsProperties;

  @Bean
  WebMvcConfigurer corsConfigurer() {
    final String[] allowedOrigins = corsProperties.getAllowedOrigins();
    if (log.isInfoEnabled()) {
      log.info("[Cors Configuration] Allowed origins: {}", corsProperties.getAllowedOriginAsString());
    }
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedHeaders("*")
            .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE");
      }
    };
  }

}
