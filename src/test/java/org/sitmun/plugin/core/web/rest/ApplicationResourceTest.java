package org.sitmun.plugin.core.web.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sitmun.plugin.core.config.RepositoryRestConfig;
import org.sitmun.plugin.core.domain.*;
import org.sitmun.plugin.core.repository.*;
import org.sitmun.plugin.core.security.AuthoritiesConstants;
import org.sitmun.plugin.core.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.sitmun.plugin.core.security.SecurityConstants.HEADER_STRING;
import static org.sitmun.plugin.core.security.SecurityConstants.TOKEN_PREFIX;
import static org.sitmun.plugin.core.test.TestConstants.SITMUN_ADMIN_USERNAME;
import static org.sitmun.plugin.core.test.TestUtils.withMockSitmunAdmin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationResourceTest {

  private static final String APP_URI = "http://localhost/api/applications";
  private static final String CARTOGRAPHY_GROUP_URI = "http://localhost/api/cartography-groups";
  private static final String NON_PUBLIC_APPLICATION_NAME = "Non-public Application";
  private static final String PUBLIC_APPLICATION_NAME = "Public Application";
  private static final String NON_PUBLIC_APPLICATION_PARAM_NAME = "Non-public Application Param";
  private static final String PUBLIC_APPLICATION_PARAM_NAME = "Public Application Param";
  private static final String PUBLIC_TREE_NAME = "Public tree";
  private static final String PUBLIC_BACKGROUND_NAME = "Public Background Name";
  private static final String PUBLIC_BACKGROUND_MAP_NAME = "Public Background Map Name";
  private static final String PUBLIC_SITUATION_MAP_NAME = "Public Situation Map Name";
  private static final String PUBLIC_CARTOGRAPHY_NAME = "Public Cartography Name";
  private static final String PUBLIC_TREE_NODE_NAME = "Tree Node Name";
  private static final String TREE_NODE_URI = "http://localhost/api/tree-nodes";
  private static final String APP_BACKGROUNDS_URI = "http://localhost/api/application-backgrounds";
  private static final String PUBLIC_SERVICE_NAME = "Public Service Name";
  private static final String SERVICE_URI = "http://localhost/api/services";
  @Autowired
  ApplicationRepository applicationRepository;
  @Autowired
  TreeRepository treeRepository;
  @Autowired
  TerritoryRepository territoryRepository;
  @Autowired
  TreeNodeRepository treeNodeRepository;
  @Autowired
  ApplicationBackgroundRepository applicationBackgroundRepository;
  @Autowired
  BackgroundRepository backgroundRepository;
  @Autowired
  CartographyPermissionRepository cartographyPermissionRepository;
  @Autowired
  CartographyRepository cartographyRepository;
  @Autowired
  ServiceRepository serviceRepository;
  @Autowired
  CartographyAvailabilityRepository cartographyAvailabilityRepository;
  @Autowired
  ApplicationParameterRepository applicationParameterRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  TokenProvider tokenProvider;

  @Autowired
  private MockMvc mvc;

  private String token;
  private Integer appId;
  private Integer backAppId;
  private ArrayList<Tree> trees;
  private Set<Service> services;
  private Set<Cartography> cartographies;
  private Set<CartographyAvailability> cartographyAvailabilities;
  private Set<TreeNode> treeNodes;
  private Set<CartographyPermission> cartographyPermissions;
  private Set<Background> backgrounds;
  private ArrayList<Application> applications;
  private ArrayList<ApplicationParameter> applicationParameters;
  private Territory territory;
  private Role publicRole;

  @Before
  public void init() {
    withMockSitmunAdmin(() -> {

      token = tokenProvider.createToken(SITMUN_ADMIN_USERNAME);

      territory = Territory.builder()
          .setName("Territorio 1")
          .setCode("")
          .setBlocked(false)
          .build();
      territoryRepository.save(territory);

      applications = new ArrayList<>();
      applicationParameters = new ArrayList<>();

      publicRole = Role.builder().setName(AuthoritiesConstants.USUARIO_PUBLICO).build();
      roleRepository.save(publicRole);

      Set<Role> availableRoles = new HashSet<>();
      availableRoles.add(publicRole);

      //Trees
      trees = new ArrayList<>();
      Tree publicTree = new Tree();
      publicTree.setName(PUBLIC_TREE_NAME);
      publicTree.setAvailableRoles(availableRoles);
      trees.add(publicTree);
      this.treeRepository.saveAll(trees);
      Set<Tree> trees = new HashSet<>();
      trees.add(publicTree);

      //Services
      Service publicService = Service.builder()
          .setName(PUBLIC_SERVICE_NAME)
          .setType("")
          .setServiceURL("")
          .setBlocked(false)
          .build();
      //publicService.setLayers(cartographies);

      services = new HashSet<>();
      services.add(publicService);
      serviceRepository.saveAll(services);

      //Cartographies
      Cartography publicCartography = Cartography.builder()
          .setName(PUBLIC_CARTOGRAPHY_NAME)
          .setService(publicService)
          .setLayers(Collections.emptyList())
          .setQueryableFeatureAvailable(false)
          .setQueryableFeatureEnabled(false)
          .setBlocked(false)
          .build();

      cartographies = new HashSet<>();
      cartographies.add(publicCartography);
      this.cartographyRepository.saveAll(cartographies);
      publicCartography = cartographies.iterator().next();

      //Cartography availabilities
      CartographyAvailability publicCartographyAvailability = new CartographyAvailability();
      publicCartographyAvailability.setCartography(publicCartography);
      publicCartographyAvailability.setTerritory(territory);

      cartographyAvailabilities = new HashSet<>();
      cartographyAvailabilities.add(publicCartographyAvailability);
      this.cartographyAvailabilityRepository.saveAll(cartographyAvailabilities);
      // publicCartographyAvailability = cartographyAvailabilities.iterator().next();

      //Tree nodes
      treeNodes = new HashSet<>();
      TreeNode publicTreeNode = new TreeNode();
      publicTreeNode.setName(PUBLIC_TREE_NODE_NAME);
      publicTreeNode.setCartography(publicCartography);
      publicTreeNode.setTree(publicTree);
      treeNodes.add(publicTreeNode);
      this.treeNodeRepository.saveAll(treeNodes);
      // publicTreeNode = treeNodes.iterator().next();

      //Cartography group
      cartographyPermissions = new HashSet<>();

      BackgroundMap publicBackgroundMap = new BackgroundMap();
      publicBackgroundMap.setName(PUBLIC_BACKGROUND_MAP_NAME);
      publicBackgroundMap.setRoles(availableRoles);
      publicBackgroundMap.setMembers(cartographies);
      publicBackgroundMap = cartographyPermissionRepository.save(publicBackgroundMap);
      cartographyPermissions.add(publicBackgroundMap);


      //backgrounds
      backgrounds = new HashSet<>();
      Background publicBackground = new Background();
      publicBackground.setName(PUBLIC_BACKGROUND_NAME);
      publicBackground.setCartographyGroup(publicBackgroundMap);
      backgrounds.add(publicBackground);
      backgroundRepository.saveAll(backgrounds);
      publicBackground = backgrounds.iterator().next();

      Application application = Application.builder()
          .setName(NON_PUBLIC_APPLICATION_NAME)
          .setType("I")
          .setJspTemplate("")
          .build();
      SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
      String dateInString = "Friday, Jun 7, 2013 12:10:56 PM";
      try {
        application.setCreatedDate(formatter.parse(dateInString));
      } catch (Exception e) {
        e.printStackTrace();
      }
      applications.add(application);

      SituationMap publicSituaionMap = new SituationMap();
      publicSituaionMap.setName(PUBLIC_SITUATION_MAP_NAME);
      publicSituaionMap.setRoles(availableRoles);
      publicSituaionMap.setMembers(cartographies);
      publicSituaionMap = cartographyPermissionRepository.save(publicSituaionMap);
      cartographyPermissions.add(publicSituaionMap);

      Application publicApplication = Application.builder()
          .setType("I")
          .setName(PUBLIC_APPLICATION_NAME)
          .setAvailableRoles(availableRoles)
          .setTrees(trees)
          .setSituationMap(publicSituaionMap)
          .setJspTemplate("")
          .setCreatedDate(Date.from(Instant.now()))
          .build();

      applications.add(publicApplication);
      applicationRepository.saveAll(applications);

      appId = applications.get(0).getId();

      //application backgrounds
      Set<ApplicationBackground> applicationBackgrounds = new HashSet<>();
      ApplicationBackground publicApplicationBackground = new ApplicationBackground();
      publicApplicationBackground.setBackground(publicBackground);
      publicApplicationBackground.setApplication(publicApplication);
      publicApplicationBackground.setOrder(1);
      applicationBackgrounds.add(publicApplicationBackground);
      applicationBackgroundRepository.saveAll(applicationBackgrounds);

      backAppId = publicApplicationBackground.getId();

      ApplicationParameter applicationParam1 = new ApplicationParameter();
      applicationParam1.setName(NON_PUBLIC_APPLICATION_PARAM_NAME);
      applicationParam1.setApplication(application);
      applicationParam1.setValue("");
      applicationParam1.setType("");
      applicationParameters.add(applicationParam1);

      ApplicationParameter applicationParam2 = new ApplicationParameter();
      applicationParam2.setName(PUBLIC_APPLICATION_PARAM_NAME);
      applicationParam2.setApplication(publicApplication);
      applicationParam2.setValue("");
      applicationParam2.setType("");
      applicationParameters.add(applicationParam2);

      applicationParameterRepository.saveAll(applicationParameters);
      // Create user
      // Create territory
      // Create role
      // Create application
    });
  }

  @After
  public void cleanup() {
    withMockSitmunAdmin(() -> {
      applicationParameters
          .forEach((item) -> applicationParameterRepository.deleteById(item.getId()));
      applications.forEach((item) -> applicationRepository.deleteById(item.getId()));
      backgrounds.forEach((item) -> backgroundRepository.deleteById(item.getId()));
      cartographyPermissions
          .forEach((item) -> cartographyPermissionRepository.deleteById(item.getId()));
      cartographyAvailabilities
          .forEach((item) -> cartographyAvailabilityRepository.deleteById(item.getId()));
      treeNodes.forEach((item) -> treeNodeRepository.deleteById(item.getId()));
      trees.forEach((item) -> treeRepository.deleteById(item.getId()));
      cartographies.forEach((item) -> cartographyRepository.deleteById(item.getId()));
      services.forEach((item) -> serviceRepository.deleteById(item.getId()));
      territoryRepository.delete(territory);
      roleRepository.delete(publicRole);
    });
  }

  @Ignore
  public void getPublicApplicationsAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(APP_URI))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.applications", hasSize(0)));
  }

  @Ignore
  public void getPublicApplicationParamsAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(APP_URI + "/2/parameters"))
        .andExpect(status().isOk());
  }

  @Ignore
  @WithMockUser(username = SITMUN_ADMIN_USERNAME)
  public void getInformationAboutAnApp() throws Exception {
    mvc.perform(get(APP_URI + "/" + appId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Non-public Application"))
        .andExpect(jsonPath("$.createdDate").value("2013-06-07T10:10:56.000+0000"));
  }

  @Test
  public void getInformationAboutBackgrounds() throws Exception {
    mvc.perform(get(APP_BACKGROUNDS_URI + "/" + backAppId)
        .header(HEADER_STRING, TOKEN_PREFIX + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.order").value(1));
  }

  @Ignore
  public void getPublicApplicationTreesAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(APP_URI + "/2/trees"))
        .andExpect(status().isOk());
  }

  @Ignore
  public void getPublicApplicationBackgroundsAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(APP_URI + "/2/backgrounds"))
        .andExpect(status().isOk());
  }

  @Ignore
  public void getPublicApplicationSituationMapsAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(APP_URI + "/2/situationMap"))
        .andExpect(status().isOk());
  }

  @Ignore
  public void getCartographyGroupMembersAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(CARTOGRAPHY_GROUP_URI + "/1/members"))
        .andExpect(status().isOk());
  }

  @Ignore
  public void getTreeNodeCartographyAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(TREE_NODE_URI + "/1/cartography"))
        .andExpect(status().isOk());
  }

  @Test
  public void getServiceLayersAsPublic() throws Exception {
    // TODO
    // ok is expected
    mvc.perform(get(SERVICE_URI + "/1/layers"))
        .andExpect(status().isOk());
  }

  @Ignore
  public void getApplicationsAsTerritorialUser() {
    // TODO
    // ok is expected
  }

  @Test
  public void getApplicationsAsSitumunAdmin() throws Exception {
    // ok is expected
    mvc.perform(get(APP_URI)
        .header(HEADER_STRING, TOKEN_PREFIX + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.applications", hasSize(36)));
  }

  @Ignore
  public void getApplicationsAsOrganizationAdmin() {
    // TODO
    // ok is expected
  }

  @Ignore
  public void setAvailableRolesAsPublicFails() {
    // TODO
    // fail is expected
  }

  @Ignore
  public void setAvailableRolesAsTerritorialUserFails() {
    // TODO
    // fail is expected
  }

  @Ignore
  public void setAvailableRolesAsSitmunAdmin() {
    // TODO Update available roles for the app as an admin user
    // ok is expected
  }

  @Ignore
  public void setTreeAsSitmunAdmin() {
    // TODO Update tree for the app as an admin user
    // ok is expected
  }

  @Ignore
  public void setBackgroundAsSitmunAdmin() {
    // TODO:Update background for the app as an admin user
    // ok is expected
  }

  @Ignore
  public void setAvailableRolesAsOrganizationAdmin() {
    // TODO: Update available roles for the app (linked to the same organization) as an organization admin user
    // ok is expected
  }

  @Ignore
  public void setTreeAsOrganizationAdmin() {
    // TODO: Update tree for the app (linked to the same organization) as an organization admin user
    // ok is expected
  }

  @Ignore
  public void setBackgroundAsOrganizationAdmin() {
    // TODO: Update background for the app (linked to the same organization) as an organization admin user
    // ok is expected
  }

  @Ignore
  public void setAvailableRolesAsOtherOrganizationAdminFails() {
    // TODO: Update available roles for the app (linked to another organization) as an organization admin user
    // fail is expected
  }

  @Ignore
  public void setTreeAsOtherOrganizationAdminFails() {
    // TODO: Update tree for the app (linked to another organization) as an organization admin user
    // fail is expected
  }

  @Ignore
  public void setBackgroundAsOtherOrganizationAdminFails() {
    // TODO: Update background for the app (linked to another organization) as an organization admin user
    // fail is expected
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
