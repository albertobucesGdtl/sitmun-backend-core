package org.sitmun.plugin.core.config;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;

@Configuration
@Profile("heroku")
public class HerokuLiquibaseConfig {

  @Value("${spring.liquibase.changelog}")
  private String changeLog;

  @Bean
  public SpringLiquibase liquibase(DataSource datasource, TaskExecutor taskExecutor) {
    SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor);
    liquibase.setChangeLog(changeLog);
    liquibase.setDataSource(datasource);
    return liquibase;
  }
}

class AsyncSpringLiquibase extends SpringLiquibase {

  private final TaskExecutor taskExecutor;

  public AsyncSpringLiquibase(TaskExecutor taskExecutor) {
    super();
    this.taskExecutor = taskExecutor;
  }

  @Override
  public void afterPropertiesSet() {
    taskExecutor.execute(() -> {
      try {
        log.info("Starting Liquibase asynchronously, your database might not be ready at startup!");
        initDb();
      } catch (LiquibaseException e) {
        log.severe("Liquibase could not start correctly, your database is NOT ready: " + e.getMessage(), e);
      }
    });
  }

  protected void initDb() throws LiquibaseException {
    StopWatch watch = new StopWatch();
    watch.start();
    super.afterPropertiesSet();
    watch.stop();
    log.info("Started Liquibase in " + watch.getTotalTimeMillis() + "ms");
  }
}
