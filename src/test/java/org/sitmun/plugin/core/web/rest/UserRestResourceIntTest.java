package org.sitmun.plugin.core.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.sitmun.plugin.core.security.SecurityConstants.HEADER_STRING;
import static org.sitmun.plugin.core.security.SecurityConstants.SITMUN_ADMIN_USERNAME;
import static org.sitmun.plugin.core.security.SecurityConstants.TOKEN_PREFIX;
import static org.sitmun.plugin.core.test.TestUtils.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.Before;
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
import org.sitmun.plugin.core.security.AuthoritiesConstants;
import org.sitmun.plugin.core.security.SecurityConstants;
import org.sitmun.plugin.core.security.TokenProvider;
import org.sitmun.plugin.core.service.UserService;
import org.sitmun.plugin.core.service.dto.UserDTO;
import org.sitmun.plugin.core.web.rest.dto.PasswordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserRestResourceIntTest {

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

  private static final String USER_USERNAME = "admin";
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
  TokenProvider tokenProvider;
  @Autowired
  private MockMvc mvc;
  private String token;
  private User sitmunAdmin;

  private User organizacionAdmin;

  private User territory1User;

  private User territory2User;

  @Value("${default.territory.name}")
  private String defaultTerritoryName;

  private Territory defaultTerritory;
  private Territory territory1;
  private Territory territory2;
  private Role sitmunAdminRole;
  private Role organizacionAdminRole;
  private Role territorialRole;

  @Before
  @WithMockUser(username = USER_USERNAME)
  public void init() {
    token = tokenProvider.createToken(USER_USERNAME);
    sitmunAdminRole = this.roleRepository.findOneByName(AuthoritiesConstants.ADMIN_SITMUN).get();

    organizacionAdminRole =
        this.roleRepository.findOneByName(AuthoritiesConstants.ADMIN_ORGANIZACION).get();

    territorialRole =
        this.roleRepository.findOneByName(AuthoritiesConstants.ADMIN_ORGANIZACION).get();

    defaultTerritory = territoryRepository.findOneByName(this.defaultTerritoryName).get();
    ArrayList<Territory> territoriesToCreate = new ArrayList<>();
    ArrayList<User> usersToCreate = new ArrayList<>();
    territory1 = Territory.builder()
        .setName("Territorio 1")
        .setCode("")
        .setBlocked(false)
        .build();

    territory2 =  Territory.builder()
        .setName("Territorio 2")
        .setCode("")
        .setBlocked(false)
        .build();
    territoriesToCreate.add(territory1);
    territoriesToCreate.add(territory2);

    territoryRepository.saveAll(territoriesToCreate);

    // Sitmun Admin
    sitmunAdmin = this.userRepository.findOneWithPermissionsByUsername(USER_USERNAME).get();

    // Territory 1 Admin
    organizacionAdmin = new User();
    organizacionAdmin.setAdministrator(USER_ADMINISTRATOR);
    organizacionAdmin.setBlocked(USER_BLOCKED);
    organizacionAdmin.setFirstName(USER_FIRSTNAME);
    organizacionAdmin.setLastName(USER_LASTNAME);
    organizacionAdmin.setPassword(USER_PASSWORD);
    organizacionAdmin.setUsername(TERRITORY1_ADMIN_USERNAME);
    usersToCreate.add(organizacionAdmin);

    // Territory 1 user
    territory1User = new User();
    territory1User.setAdministrator(false);
    territory1User.setBlocked(USER_BLOCKED);
    territory1User.setFirstName(USER_FIRSTNAME);
    territory1User.setLastName(USER_LASTNAME);
    territory1User.setPassword(USER_PASSWORD);
    territory1User.setUsername(TERRITORY1_USER_USERNAME);
    usersToCreate.add(territory1User);

    // Territory 2 user
    territory2User = new User();
    territory2User.setAdministrator(false);
    territory2User.setBlocked(USER_BLOCKED);
    territory2User.setFirstName(USER_FIRSTNAME);
    territory2User.setLastName(USER_LASTNAME);
    territory2User.setPassword(USER_PASSWORD);
    territory2User.setUsername(TERRITORY2_USER_USERNAME);
    usersToCreate.add(territory2User);

    userRepository.saveAll(usersToCreate);

    UserConfiguration userConf = new UserConfiguration();
    userConf.setTerritory(territory1);
    userConf.setRole(organizacionAdminRole);
    userConf.setUser(organizacionAdmin);
    this.userConfigurationRepository.save(userConf);

    userConf = new UserConfiguration();
    userConf.setTerritory(territory1);
    userConf.setRole(territorialRole);
    userConf.setUser(territory1User);
    this.userConfigurationRepository.save(userConf);

    userConf = new UserConfiguration();
    userConf.setTerritory(territory2);
    userConf.setRole(territorialRole);
    userConf.setUser(territory2User);

    this.userConfigurationRepository.save(userConf);
  }

  @Test
  @WithMockUser(username = USER_USERNAME, authorities = {SITMUN_ADMIN_USERNAME})
  public void createNewUserAndDelete() throws Exception {
    UserDTO newUser = new UserDTO(sitmunAdmin);
    newUser.setId(null);
    newUser.setUsername(NEW_USER_USERNAME);

    String uri = mvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(newUser))
    ).andExpect(status().isCreated())
        .andReturn().getResponse().getHeader("Location");

    assertThat(uri).isNotNull();

    mvc.perform(get(uri)
        .header(HEADER_STRING, TOKEN_PREFIX + token)
    ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.username", equalTo(NEW_USER_USERNAME)));

    mvc.perform(delete(uri)
        .header(HEADER_STRING, TOKEN_PREFIX + token)
    ).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = USER_USERNAME)
  public void createDuplicatedUserFails() throws Exception {
    UserDTO newUser = new UserDTO(sitmunAdmin);
    newUser.setId(null);

    mvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(newUser)))
        .andExpect(status().isConflict());
  }

  @Test
  public void getUsers() throws Exception {
    mvc.perform(get("/api/users")
        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
    ).andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = USER_USERNAME)
  public void updateUser() throws Exception {
    UserDTO userDTO = new UserDTO(sitmunAdmin);
    userDTO.setFirstName(USER_CHANGEDFIRSTNAME);
    userDTO.setLastName(USER_CHANGEDLASTNAME);

    mvc.perform(put(USER_URI + "/" + sitmunAdmin.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userDTO))
    ).andExpect(status().isNoContent());

    mvc.perform(get(USER_URI + "/" + sitmunAdmin.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.firstName", equalTo(USER_CHANGEDFIRSTNAME)))
        .andExpect(jsonPath("$.lastName", equalTo(USER_CHANGEDLASTNAME)));
  }

  @Test
  @WithMockUser(username = USER_USERNAME)
  public void getUsersAsSitmunAdmin() throws Exception {
    mvc.perform(get(USER_URI))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.users", hasSize(5)));
  }

  @Test
  @WithMockUser(username = TERRITORY1_ADMIN_USERNAME)
  public void getUsersAsOrganizationAdmin() throws Exception {
    mvc.perform(get(USER_URI))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded.users", hasSize(1)));
  }

  @Test
  @WithMockUser(username = USER_USERNAME)
  public void updateUserPassword() throws Exception {
    PasswordDTO passwordDTO = new PasswordDTO();
    passwordDTO.setPassword(USER_CHANGEDPASSWORD);

    mvc.perform(post(USER_URI + "/" + sitmunAdmin.getId() + "/change-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(passwordDTO))
    ).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = USER_USERNAME)
  public void updateUserPasswordAsSitmunAdmin() throws Exception {
    PasswordDTO passwordDTO = new PasswordDTO();
    passwordDTO.setPassword(USER_CHANGEDPASSWORD);

    mvc.perform(post(USER_URI + "/" + sitmunAdmin.getId() + "/change-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(passwordDTO))
    ).andExpect(status().isOk());
  }


  @Test
  public void createNewUserAsOrganizationAdmin() {
    // TODO: Create new user by an organization admin user (ADMIN DE ORGANIZACION)
    // ok is expected. The new user has roles linked to my organization territory
  }

  @Test
  public void assignRoleToUserAsOrganizationAdmin() {
    // TODO
    // ok is expected. The new user has roles linked to my organization territory
  }

  @Test
  public void updateUserAsOrganizationAdmin() {
    // TODO
    // Update user (linked to the same organization) by an organization admin user
    // (ADMIN DE ORGANIZACION)
    // ok is expected
  }

  @Test
  public void updateUserPasswordAsOrganizationAdmin() {
    // TODO
    // Update user password (linked to the same organization) by an organization
    // admin user (ADMIN DE ORGANIZACION)
    // ok is expected
  }

  @Test
  public void assignRoleToUserAsOtherOrganizationAdminFails() {
    // TODO
    // fail is expected. No permission to assign territory role to user if don't
    // have territory role
  }

  @Test
  public void updateUserAsOtherOrganizationAdminFails() {
    // TODO
    // Update user (linked to another organization) by an organization admin user
    // (ADMIN DE ORGANIZACION)
    // fail is expected (no permission)
  }

  @Test
  public void updateUserPasswordAsOtherOrganizationAdminFails() {
    // TODO
    // Update user password (linked to another organization) by an organization
    // admin user (ADMIN DE ORGANIZACION)
    // fail is expected (no permission)
  }
}
