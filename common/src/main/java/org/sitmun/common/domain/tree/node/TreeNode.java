package org.sitmun.common.domain.tree.node;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.sitmun.common.def.PersistenceConstants;
import org.sitmun.common.domain.cartography.Cartography;
import org.sitmun.common.domain.tree.Tree;
import org.sitmun.common.types.basic.Http;
import org.sitmun.feature.client.config.Views;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Tree node.
 */
@Entity
@Table(name = "STM_TREE_NOD")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeNode {

  /**
   * Unique identifier.
   */
  @TableGenerator(
    name = "STM_TREE_NOD_GEN",
    table = "STM_SEQUENCE",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "TNO_ID",
    allocationSize = 1)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "STM_TREE_NOD_GEN")
  @Column(name = "TNO_ID")
  @JsonView(Views.WorkspaceApplication.class)
  private Integer id;

  /**
   * Parent node.
   */
  @JoinColumn(name = "TNO_PARENTID", foreignKey = @ForeignKey(name = "STM_TNO_FK_TNO"))
  @ManyToOne(fetch = FetchType.LAZY)
  private TreeNode parent;

  /**
   * Name.
   */
  @Column(name = "TNO_NAME", length = 80)
  @NotBlank
  @JsonView(Views.WorkspaceApplication.class)
  private String name;

  /**
   * Description.
   */
  @Column(name = "TNO_ABSTRACT", length = PersistenceConstants.SHORT_DESCRIPTION)
  private String description;

  /**
   * Tooltip text.
   */
  @Column(name = "TNO_TOOLTIP", length = 100)
  private String tooltip;

  /**
   * Enabled by default.
   */
  @Column(name = "TNO_ACTIVE")
  private Boolean active;

  /**
   * Radio button type (only if the node is a folder).
   */
  @Column(name = "TNO_RADIO")
  private Boolean radio;

  /**
   * Order of the node within the tree.
   */
  @Column(name = "TNO_ORDER", precision = 6)
  private Integer order;

  /**
   * URL to metadata.
   */
  @Column(name = "TNO_METAURL", length = PersistenceConstants.URL)
  @Http
  private String metadataURL;

  /**
   * URL to downloadable (zip) dataset.
   */
  @Column(name = "TNO_DATAURL", length = PersistenceConstants.URL)
  @Http
  private String datasetURL;

  /**
   * Enable GetMap Filter (if available).
   */
  @Column(name = "TNO_FILTER_GM")
  private Boolean filterGetMap;

  /**
   * Enable GetFeatureInfo Filter (if available).
   */
  @Column(name = "TNO_FILTER_GFI")
  private Boolean filterGetFeatureInfo;

  /**
   * Enable GetFeatureInfo (if available).
   */
  @Column(name = "TNO_QUERYACT")
  private Boolean queryableActive;

  /**
   * Enable Selectable Filter (if available).
   */
  @Column(name = "TNO_FILTER_SE")
  private Boolean filterSelectable;

  /**
   * Style name.
   */
  @Column(name = "TNO_STYLE")
  private String style;

  /**
   * Tree.
   */
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "TNO_TREEID", foreignKey = @ForeignKey(name = "STM_TNO_FK_TRE"))
  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private Tree tree;

  /**
   * Cartography associated to this node.
   */
  @JoinColumn(name = "TNO_GIID", foreignKey = @ForeignKey(name = "STM_TNO_FK_GEO"))
  @ManyToOne(fetch = FetchType.LAZY)
  private Cartography cartography;

  @JsonView(Views.WorkspaceApplication.class)
  public Integer getParentId() {
    if (parent != null) {
      return parent.id;
    }
    return null;
  }

  @JsonView(Views.WorkspaceApplication.class)
  public Integer getCartographyId() {
    if (cartography != null) {
      return cartography.getId();
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof TreeNode))
      return false;

    TreeNode other = (TreeNode) o;

    return id != null &&
      id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
