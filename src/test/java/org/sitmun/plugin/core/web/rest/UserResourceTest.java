package org.sitmun.plugin.core.web.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sitmun.plugin.core.config.RepositoryRestConfig;
import org.sitmun.plugin.core.domain.Role;
import org.sitmun.plugin.core.domain.Territory;
import org.sitmun.plugin.core.domain.User;
import org.sitmun.plugin.core.domain.UserConfiguration;
import org.sitmun.plugin.core.repository.RoleRepository;
import org.sitmun.plugin.core.repository.TerritoryRepository;
import org.sitmun.plugin.core.repository.UserConfigurationRepository;
import org.sitmun.plugin.core.repository.UserRepository;
import org.sitmun.plugin.core.repository.handlers.UserEventHandler;
import org.sitmun.plugin.core.security.AuthoritiesConstants;
import org.sitmun.plugin.core.service.UserService;
import org.sitmun.plugin.core.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.sitmun.plugin.core.test.TestConstants.SITMUN_ADMIN_USERNAME;
import static org.sitmun.plugin.core.test.TestUtils.asJsonString;
import static org.sitmun.plugin.core.test.TestUtils.withMockSitmunAdmin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UserResourceTest {

  private static final String TERRITORY1_ADMIN_USERNAME = "territory1-admin";
  private static final String TERRITORY1_USER_USERNAME = "territory1-user";
  private static final String TERRITORY2_USER_USERNAME = "territory2-user";
  private static final String NEW_USER_USERNAME = "admin_new";
  private static final String USER_PASSWORD = "admin";
  private static final String USER_CHANGEDPASSWORD = "nimda";
  private static final String USER_FIRSTNAME = "Admin";
  private static final String USER_CHANGEDFIRSTNAME = "Administrator";
  private static final String USER_LASTNAME = "Admin";
  private static final String USER_CHANGEDLASTNAME = "Territory 1";
  private static final Boolean USER_BLOCKED = false;
  private static final Boolean USER_ADMINISTRATOR = true;
  private static final String USER_URI = "http://localhost/api/users";
  @Autowired
  UserRepository userRepository;
  @Autowired
  UserConfigurationRepository userConfigurationRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  TerritoryRepository territoryRepository;
  @Autowired
  UserService userService;
  @Autowired
  private MockMvc mvc;
  private User organizacionAdmin;
  private User territory1User;
  private User territory2User;

  private Territory territory1;
  private Territory territory2;


  private Role organizacionAdminRole;
  private Role territorialRole;

  private ArrayList<Territory> territories;
  private ArrayList<User> users;
  private ArrayList<UserConfiguration> userConfigurations;

  @Autowired
  private UserEventHandler userEventHandler;

  @Before
  public void init() {
    withMockSitmunAdmin(() -> {

      organizacionAdminRole =
        Role.builder().setName(AuthoritiesConstants.ADMIN_ORGANIZACION).build();
      roleRepository.save(organizacionAdminRole);

      territorialRole = Role.builder().setName(AuthoritiesConstants.USUARIO_TERRITORIAL).build();
      roleRepository.save(territorialRole);

      territories = new ArrayList<>();
      users = new ArrayList<>();
      territory1 = Territory.builder()
        .setName("Territorio 1")
        .setCode("")
        .setBlocked(false)
        .build();

      territory2 = Territory.builder()
        .setName("Territorio 2")
        .setCode("")
        .setBlocked(false)
        .build();
      territories.add(territory1);
      territories.add(territory2);

      territoryRepository.saveAll(territories);

      // Territory 1 Admin
      organizacionAdmin = new User();
      organizacionAdmin.setAdministrator(USER_ADMINISTRATOR);
      organizacionAdmin.setBlocked(USER_BLOCKED);
      organizacionAdmin.setFirstName(USER_FIRSTNAME);
      organizacionAdmin.setLastName(USER_LASTNAME);
      organizacionAdmin.setPassword(USER_PASSWORD);
      organizacionAdmin.setUsername(TERRITORY1_ADMIN_USERNAME);
      userEventHandler.handleUserCreate(organizacionAdmin);
      organizacionAdmin = userRepository.save(organizacionAdmin);
      users.add(organizacionAdmin);

      // Territory 1 user
      territory1User = new User();
      territory1User.setAdministrator(false);
      territory1User.setBlocked(USER_BLOCKED);
      territory1User.setFirstName(USER_FIRSTNAME);
      territory1User.setLastName(USER_LASTNAME);
      territory1User.setPassword(USER_PASSWORD);
      territory1User.setUsername(TERRITORY1_USER_USERNAME);
      territory1User = userRepository.save(territory1User);
      users.add(territory1User);

      // Territory 2 user
      territory2User = new User();
      territory2User.setAdministrator(false);
      territory2User.setBlocked(USER_BLOCKED);
      territory2User.setFirstName(USER_FIRSTNAME);
      territory2User.setLastName(USER_LASTNAME);
      territory2User.setPassword(USER_PASSWORD);
      territory2User.setUsername(TERRITORY2_USER_USERNAME);
      territory2User = userRepository.save(territory2User);
      users.add(territory2User);


      userConfigurations = new ArrayList<>();

      UserConfiguration userConf = UserConfiguration.builder()
        .setTerritory(territory1)
        .setRole(organizacionAdminRole)
        .setUser(organizacionAdmin)
        .setAppliesToChildrenTerritories(false)
        .build();
      userConfigurations.add(userConf);
      this.userConfigurationRepository.save(userConf);

      userConf = UserConfiguration.builder()
        .setTerritory(territory1)
        .setRole(territorialRole)
        .setUser(territory1User)
        .setAppliesToChildrenTerritories(false)
        .build();
      userConfigurations.add(userConf);
      this.userConfigurationRepository.save(userConf);

      userConf = UserConfiguration.builder()
        .setTerritory(territory2)
        .setRole(territorialRole)
        .setUser(territory2User)
        .setAppliesToChildrenTerritories(false)
        .build();
      userConfigurations.add(userConf);

      userConfigurationRepository.saveAll(userConfigurations);
    });
  }

  @After
  public void cleanup() {
    withMockSitmunAdmin(() -> {
      userConfigurationRepository.deleteAll(userConfigurations);
      roleRepository.delete(territorialRole);
      roleRepository.delete(organizacionAdminRole);
      userRepository.deleteAll(users);
      territoryRepository.deleteAll(territories);
    });
  }


  @Test
  public void createNewUserAndDelete() throws Exception {
    UserDTO newUser = new UserDTO(organizacionAdmin);
    newUser.setId(null);
    newUser.setUsername(NEW_USER_USERNAME);

    String uri = mvc.perform(post("/api/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(newUser))
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isCreated())
      .andReturn().getResponse().getHeader("Location");

    assertThat(uri).isNotNull();

    mvc.perform(get(uri)).andExpect(status().isOk())
      .andExpect(content().contentType(MediaTypes.HAL_JSON))
      .andExpect(jsonPath("$.username", equalTo(NEW_USER_USERNAME)));

    mvc.perform(delete(uri)
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isNoContent());
  }

  @Test
  public void createDuplicatedUserFails() throws Exception {
    UserDTO newUser = new UserDTO(organizacionAdmin);
    newUser.setId(null);

    mvc.perform(post("/api/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(newUser))
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    )
      .andExpect(status().isConflict());
  }

  @Test
  public void updateUser() throws Exception {
    String content = "{" +
      "\"username\":\"user\"," +
      "\"firstName\":\"" + USER_CHANGEDFIRSTNAME + "\"," +
      "\"lastName\":\"" + USER_CHANGEDLASTNAME + "\"," +
      "\"administrator\": false," +
      "\"blocked\": false}";

    mvc.perform(put(USER_URI + "/" + organizacionAdmin.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(content)
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isOk());

    mvc.perform(get(USER_URI + "/" + organizacionAdmin.getId()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaTypes.HAL_JSON))
      .andExpect(jsonPath("$.firstName", equalTo(USER_CHANGEDFIRSTNAME)))
      .andExpect(jsonPath("$.lastName", equalTo(USER_CHANGEDLASTNAME)));
  }

  @Test
  public void getUsersAsSitmunAdmin() throws Exception {
    mvc.perform(get(USER_URI + "?size=10")
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaTypes.HAL_JSON))
      .andExpect(jsonPath("$._embedded.users", hasSize(10)));
  }

  @Deprecated
  @Test
  @Ignore
  public void getUsersAsOrganizationAdmin() throws Exception {
    mvc.perform(get(USER_URI))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaTypes.HAL_JSON))
      .andExpect(jsonPath("$._embedded.users", hasSize(5)));
  }

  @Test
  public void updateUserPassword() throws Exception {
    String content = "{" +
      "\"username\":\"user\"," +
      "\"firstName\":\"NameChanged\"," +
      "\"lastName\":\"NameChanged\"," +
      "\"password\":\"new-password\"," +
      "\"administrator\": false," +
      "\"blocked\": false}";

    mvc.perform(put(USER_URI + "/" + organizacionAdmin.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(content)
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isOk());

    String oldPassword = organizacionAdmin.getPassword();
    assertNotNull(oldPassword);
    withMockSitmunAdmin(() -> {
      Optional<User> updatedUser = userRepository.findById(organizacionAdmin.getId());
      assertTrue(updatedUser.isPresent());
      assertNotEquals(oldPassword, updatedUser.get().getPassword());
    });
  }

  @Test
  public void keepPassword() throws Exception {
    String content = "{" +
      "\"username\":\"user\"," +
      "\"firstName\":\"NameChanged\"," +
      "\"lastName\":\"NameChanged\"," +
      "\"administrator\": false," +
      "\"blocked\": false}";

    mvc.perform(put(USER_URI + "/" + organizacionAdmin.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content(content)
      .with(SecurityMockMvcRequestPostProcessors.user(SITMUN_ADMIN_USERNAME))
    ).andExpect(status().isOk());

    String oldPassword = organizacionAdmin.getPassword();
    assertNotNull(oldPassword);
    withMockSitmunAdmin(() -> {
      Optional<User> updatedUser = userRepository.findById(organizacionAdmin.getId());
      assertTrue(updatedUser.isPresent());
      assertEquals(oldPassword, updatedUser.get().getPassword());
    });
  }

  @Test
  @Ignore
  public void createNewUserAsOrganizationAdmin() {
    // TODO: Create new user by an organization admin user (ADMIN DE ORGANIZACION)
    // ok is expected. The new user has roles linked to my organization territory
  }

  @Test
  @Ignore
  public void assignRoleToUserAsOrganizationAdmin() {
    // TODO
    // ok is expected. The new user has roles linked to my organization territory
  }

  @Test
  @Ignore
  public void updateUserAsOrganizationAdmin() {
    // TODO
    // Update user (linked to the same organization) by an organization admin user
    // (ADMIN DE ORGANIZACION)
    // ok is expected
  }

  @Test
  @Ignore
  public void updateUserPasswordAsOrganizationAdmin() {
    // TODO
    // Update user password (linked to the same organization) by an organization
    // admin user (ADMIN DE ORGANIZACION)
    // ok is expected
  }

  @Test
  @Ignore
  public void assignRoleToUserAsOtherOrganizationAdminFails() {
    // TODO
    // fail is expected. No permission to assign territory role to user if don't
    // have territory role
  }

  @Test
  @Ignore
  public void updateUserAsOtherOrganizationAdminFails() {
    // TODO
    // Update user (linked to another organization) by an organization admin user
    // (ADMIN DE ORGANIZACION)
    // fail is expected (no permission)
  }

  @Test
  @Ignore
  public void updateUserPasswordAsOtherOrganizationAdminFails() {
    // TODO
    // Update user password (linked to another organization) by an organization
    // admin user (ADMIN DE ORGANIZACION)
    // fail is expected (no permission)
  }

  @TestConfiguration
  static class ContextConfiguration {
    @Bean
    public Validator validator() {
      return new LocalValidatorFactoryBean();
    }

    @Bean
    RepositoryRestConfigurer repositoryRestConfigurer() {
      return new RepositoryRestConfig(validator());
    }
  }
}
