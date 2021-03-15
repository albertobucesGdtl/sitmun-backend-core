package org.sitmun.plugin.core.repository.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sitmun.plugin.core.domain.Task;
import org.sitmun.plugin.core.domain.TaskAvailability;
import org.sitmun.plugin.core.domain.TaskParameter;
import org.sitmun.plugin.core.domain.Territory;
import org.sitmun.plugin.core.repository.TaskAvailabilityRepository;
import org.sitmun.plugin.core.repository.TaskParameterRepository;
import org.sitmun.plugin.core.repository.TaskRepository;
import org.sitmun.plugin.core.repository.TerritoryRepository;
import org.sitmun.plugin.core.test.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.sitmun.plugin.core.test.TestConstants.SITMUN_ADMIN_USERNAME;
import static org.sitmun.plugin.core.test.TestUtils.asJsonString;
import static org.sitmun.plugin.core.test.TestUtils.withMockSitmunAdmin;
import static org.sitmun.plugin.core.test.URIConstants.DOWNLOAD_TASKS_URI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class TaskRepositoryDataRestTest {

  private static final String TASK_NAME = "Task Name";

  @Autowired
  TaskRepository taskRepository;
  @Autowired
  TaskAvailabilityRepository taskAvailabilityRepository;
  @Autowired
  TaskParameterRepository taskParameterRepository;
  @Autowired
  TerritoryRepository territoryRepository;
  @Autowired
  private MockMvc mvc;

  private Territory territory;
  private Task task;
  private ArrayList<Task> tasks;
  private ArrayList<TaskAvailability> availabilities;
  private ArrayList<TaskParameter> parameters;

  @BeforeEach
  public void init() {

    withMockSitmunAdmin(() -> {

      territory = Territory.builder()
        .name("Territorio 1")
        .code("")
        .blocked(false)
        .build();
      territoryRepository.save(territory);
      tasks = new ArrayList<>();
      task = new Task();
      task.setName(TASK_NAME);
      tasks.add(task);
      Task taskWithAvailabilities = new Task();
      taskWithAvailabilities.setName("Task with availabilities");
      tasks.add(taskWithAvailabilities);
      taskRepository.saveAll(tasks);

      availabilities = new ArrayList<>();
      TaskAvailability taskAvailability1 = new TaskAvailability();
      taskAvailability1.setTask(taskWithAvailabilities);
      taskAvailability1.setTerritory(territory);
      taskAvailability1.setCreatedDate(new Date());
      availabilities.add(taskAvailability1);
      taskAvailabilityRepository.saveAll(availabilities);

      parameters = new ArrayList<>();
      TaskParameter taskParam1 = new TaskParameter();
      taskParam1.setTask(task);
      taskParam1.setName("Task Param 1");
      parameters.add(taskParam1);
      TaskParameter taskParam2 = new TaskParameter();
      taskParam2.setTask(taskWithAvailabilities);
      taskParam2.setName("Task Param 2");
      parameters.add(taskParam2);
      taskParameterRepository.saveAll(parameters);
    });
  }

  @AfterEach
  public void cleanup() {
    withMockSitmunAdmin(() -> {
      taskParameterRepository.deleteAll(parameters);
      taskAvailabilityRepository.deleteAll(availabilities);
      taskRepository.deleteAll(tasks);
      territoryRepository.delete(territory);
    });
  }

  @Test
  public void postTask() throws Exception {
    String location = mvc.perform(post(URIConstants.TASKS_URI)
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(task))
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isCreated())
      .andReturn().getResponse().getHeader("Location");

    assertThat(location).isNotNull();

    mvc.perform(get(location)
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaTypes.HAL_JSON))
      .andExpect(jsonPath("$.name", equalTo(TASK_NAME)));

    withMockSitmunAdmin(() -> {
      String[] paths = URI.create(location).getPath().split("/");
      Integer id = Integer.parseInt(paths[paths.length - 1]);
      taskRepository.findById(id).ifPresent((it) -> tasks.add(it));
    });
  }

  @Disabled
  public void getTasksAsPublic() throws Exception {
    mvc.perform(get(URIConstants.TASKS_URI))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.tasks", hasSize(0)));
  }

  @Test
  public void getTasksAsSitmunAdmin() throws Exception {
    mvc.perform(get(URIConstants.TASKS_URI))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.tasks", hasSize(115)));
  }

  @Test
  public void getQueryTasksAsSitmunAdmin() throws Exception {
    mvc.perform(get(URIConstants.QUERY_TASKS_URI + "?size=10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.query-tasks", hasSize(10)));
  }

  @Test
  public void getDownloadTasksAsSitmunAdmin() throws Exception {
    mvc.perform(get(DOWNLOAD_TASKS_URI + "?size=10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.download-tasks", hasSize(10)));
  }

  @Test
  public void getTaskFilteredByTypeAsSitmunAdmin() throws Exception {
    mvc.perform(get(URIConstants.TASKS_URI_FILTER, "type.id", "2", "10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.tasks", hasSize(10)));
  }

  @Test
  public void getTaskParamsAsPublic() throws Exception {
    mvc.perform(get(URIConstants.TASK_PARAMETERS_URI, task.getId()))
      .andExpect(status().isOk());
  }

  @Test
  public void postTaskAsPublicUserFails() throws Exception {

    mvc.perform(post(URIConstants.TASKS_URI)
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(task))
    ).andExpect(status().is4xxClientError()).andReturn();
  }

  @Test
  public void getRolesOfATask() throws Exception {
    mvc.perform(get(URIConstants.TASK_ROLE_URI, 1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.roles", hasSize(39)));
  }

  @Test
  public void getPermissionsOfATask() throws Exception {
    mvc.perform(get(URIConstants.TASK_ROLE_URI, 1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.roles", hasSize(39)));
  }

}
