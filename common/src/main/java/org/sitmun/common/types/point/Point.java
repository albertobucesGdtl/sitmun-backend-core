package org.sitmun.common.types.point;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.sitmun.feature.client.config.Views;

import java.util.Objects;

/**
 * Defines a 2D point .
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@JsonView({Views.WorkspaceApplication.class})
public class Point {

  /**
   * The x-value.
   */
  private Double x;

  /**
   * The y-value.
   */
  private Double y;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Point point = (Point) o;
    return Objects.equals(x, point.x) && Objects.equals(y, point.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
