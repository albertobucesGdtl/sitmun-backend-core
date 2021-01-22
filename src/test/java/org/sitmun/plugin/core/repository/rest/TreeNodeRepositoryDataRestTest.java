package org.sitmun.plugin.core.repository.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sitmun.plugin.core.config.RepositoryRestConfig;
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

import static org.hamcrest.Matchers.hasSize;
import static org.sitmun.plugin.core.test.TestConstants.SITMUN_ADMIN_USERNAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TreeNodeRepositoryDataRestTest {

  private static final String TREE_NODE_URI =
    "http://localhost/api/tree-nodes/{0}?projection=view";

  private static final String TREE_NODE_CARTOGRAPHY_URI =
    "http://localhost/api/tree-nodes/{0}/cartography";

  private static final String TREE_ALL_NODES_URI =
    "http://localhost/api/trees/{0}/allNodes?projection=view";

  @Autowired
  private MockMvc mvc;

  @Test
  @WithMockUser(username = SITMUN_ADMIN_USERNAME)
  public void retrieveFolder() throws Exception {
    mvc.perform(get(TREE_NODE_URI, 5345))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.isFolder").value(true));
    mvc.perform(get(TREE_NODE_CARTOGRAPHY_URI, 5345))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = SITMUN_ADMIN_USERNAME)
  public void retrieveLeaf() throws Exception {
    mvc.perform(get(TREE_NODE_URI, 5351))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.isFolder").value(false));
    mvc.perform(get(TREE_NODE_CARTOGRAPHY_URI, 5351))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = SITMUN_ADMIN_USERNAME)
  public void retrieveNodesFromTree() throws Exception {
    mvc.perform(get(TREE_ALL_NODES_URI, 1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.tree-nodes", hasSize(489)))
      .andExpect(jsonPath("$._embedded.tree-nodes[?(@.isFolder == true)]", hasSize(80)))
      .andExpect(jsonPath("$._embedded.tree-nodes[?(@.isFolder == false)]", hasSize(409)));
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
