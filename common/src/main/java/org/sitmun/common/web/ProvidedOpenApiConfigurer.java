package org.sitmun.common.web;

import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"openapi-provided"})
public class ProvidedOpenApiConfigurer {

  @Bean
  SpringDocConfigProperties springDocConfigProperties() {
    return new SpringDocConfigProperties();
  }

  @Bean
  ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties){
    return new ObjectMapperProvider(springDocConfigProperties);
  }
}
