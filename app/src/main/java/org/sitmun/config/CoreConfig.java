package org.sitmun.config;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.persistence.EntityManager;

@Configuration
public class CoreConfig {

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public RepositoryRestConfig repositoryRestConfigurer(EntityManager entityManager,
                                                       ListableBeanFactory beanFactory) {
    return new RepositoryRestConfig(validator(), entityManager, beanFactory);
  }
}
