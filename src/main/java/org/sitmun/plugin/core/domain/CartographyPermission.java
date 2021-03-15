package org.sitmun.plugin.core.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.sitmun.plugin.core.constraints.CodeList;
import org.sitmun.plugin.core.constraints.CodeLists;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

import static org.sitmun.plugin.core.domain.Constants.IDENTIFIER;

/**
 * Geographic Information Permissions.
 */
@Entity
@Table(name = "STM_GRP_GI")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CartographyPermission {

  public static final String TYPE_SITUATION_MAP = "M";

  public static final String TYPE_BACKGROUND_MAP = "F";

  /**
   * Unique identifier.
   */
  @TableGenerator(
    name = "STM_GRP_GI_GEN",
    table = "STM_SEQUENCE",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "GGI_ID",
    allocationSize = 1)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "STM_GRP_GI_GEN")
  @Column(name = "GGI_ID")
  private Integer id;

  /**
   * Permissions name.
   */
  @Column(name = "GGI_NAME", length = IDENTIFIER)
  @NotBlank
  private String name;

  /**
   * Permissions type.
   */
  @Column(name = "GGI_TYPE", length = IDENTIFIER)
  @CodeList(CodeLists.CARTOGRAPHY_PERMISSION_TYPE)
  private String type;

  @JsonIgnore
  @Transient
  private String storedType;

  /**
   * The geographic information that the roles can access.
   */
  @ManyToMany
  @JoinTable(
    name = "STM_GGI_GI",
    joinColumns = @JoinColumn(
      name = "GGG_GGIID",
      foreignKey = @ForeignKey(name = "STM_GCC_FK_GCA")),
    inverseJoinColumns = @JoinColumn(
      name = "GGG_GIID",
      foreignKey = @ForeignKey(name = "STM_GCC_FK_CAR")))
  private Set<Cartography> members;

  /**
   * The the roles allowed to access the members.
   */
  @ManyToMany
  @JoinTable(
    name = "STM_ROL_GGI",
    joinColumns = @JoinColumn(
      name = "RGG_GGIID",
      foreignKey = @ForeignKey(name = "STM_RGC_FK_GCA")),
    inverseJoinColumns = @JoinColumn(
      name = "RGG_ROLEID",
      foreignKey = @ForeignKey(name = "STM_RGC_FK_ROL")))
  private Set<Role> roles;

  @OneToMany(mappedBy = "cartographyGroup")
  private Set<Background> backgrounds;

  @OneToMany(mappedBy = "situationMap")
  private Set<Application> applications;

  @PostLoad
  public void postLoad() {
    storedType = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof CartographyPermission))
      return false;

    CartographyPermission other = (CartographyPermission) o;

    return id != null &&
      id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
