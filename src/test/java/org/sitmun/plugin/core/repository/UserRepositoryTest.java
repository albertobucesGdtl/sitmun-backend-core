package org.sitmun.plugin.core.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sitmun.plugin.core.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("dev")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void init() {
    TerritoryType type = TerritoryType.builder().build();
    type.setName("tipo Territorio 1");

    Territory territory = Territory.builder()
      .name("Admin")
      .blocked(false)
      .territorialAuthorityEmail("email@email.org")
      .createdDate(new Date())
      .territorialAuthorityName("Test")
      .type(type)
      .build();

    user = new User();
    user.setFirstName("Admin");
    user.setLastName("AdminLastName");
    user.setAdministrator(true);
    user.setBlocked(false);
    user.setPassword("prCTmrOYKHQ=");
    user.setUsername("admin");
    user.setPositions(null);
    user.setPermissions(null);

    Role role = Role.builder()
      .name("rol-admin")
      .description("rol de administrador")
      .build();
    UserConfiguration conf = UserConfiguration.builder()
      .user(user)
      .role(role)
      .territory(territory)
      .appliesToChildrenTerritories(false)
      .build();

  }

  @Test
  public void saveUser() {
    assertThat(user.getId()).isNull();
    userRepository.save(user);
    assertThat(user.getId()).isNotZero();
  }

  @Test
  public void findOneUserById() {
    assertThat(user.getId()).isNull();
    userRepository.save(user);
    assertThat(user.getId()).isNotZero();

    assertThat(userRepository.findById(user.getId())).isNotNull();
  }

}
