package org.sitmun.plugin.core.domain;


import lombok.*;
import org.sitmun.plugin.core.constraints.CodeList;
import org.sitmun.plugin.core.constraints.CodeLists;
import org.sitmun.plugin.core.converters.StringListAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.sitmun.plugin.core.domain.Constants.IDENTIFIER;

/**
 * Geographic Information filter.
 */
@Entity
@Table(name = "STM_FIL_GI")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CartographyFilter {

  /**
   * Unique identifier.
   */
  @TableGenerator(
    name = "STM_FIL_GI_GEN",
    table = "STM_SEQUENCE",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "FGI_ID",
    allocationSize = 1)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "STM_FIL_GI_GEN")
  @Column(name = "FGI_ID")
  private Integer id;

  /**
   * Filter name.
   */
  @Column(name = "FGI_NAME", length = IDENTIFIER)
  @NotBlank
  private String name;

  /**
   * If <code>true</code>, this filter is required.
   */
  @Column(name = "FGI_REQUIRED")
  @NotNull
  private Boolean required;

  /**
   * Type of filter: custom or required.
   */
  @Column(name = "FGI_TYPE", length = IDENTIFIER)
  @NotNull
  @CodeList(CodeLists.CARTOGRAPHY_FILTER_TYPE)
  private String type;

  /**
   * Territorial level.
   */
  @ManyToOne
  @JoinColumn(name = "FGI_TYPID")
  private TerritoryType territorialLevel;

  /**
   * Column where the filter applies.
   */
  @Column(name = "FGI_COLUMN", length = IDENTIFIER)
  private String column;

  /**
   * A row is part of the filter if the value of the column is one of these values.
   */
  @Column(name = "FGI_VALUE", length = 4000)
  @Convert(converter = StringListAttributeConverter.class)
  private List<String> values;

  /**
   * Type of filter value.
   */
  @Column(name = "FGI_VALUETYPE", length = IDENTIFIER)
  @CodeList(CodeLists.CARTOGRAPHY_FILTER_VALUE_TYPE)
  private String valueType;

  /**
   * Cartography on which this filter can be applied.
   */
  @ManyToOne
  @JoinColumn(name = "FGI_GIID")
  @NotNull
  private Cartography cartography;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof CartographyFilter))
      return false;

    CartographyFilter other = (CartographyFilter) o;

    return id != null &&
      id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
