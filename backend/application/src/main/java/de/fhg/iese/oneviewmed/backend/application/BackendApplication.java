package de.fhg.iese.oneviewmed.backend.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "de.fhg.iese.oneviewmed.backend")
public class BackendApplication {

  public static void main(final String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

}
