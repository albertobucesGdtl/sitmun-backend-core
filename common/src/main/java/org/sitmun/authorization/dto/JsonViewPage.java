package org.sitmun.authorization.dto;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public class JsonViewPage<T> extends org.springframework.data.domain.PageImpl<T> {

  public JsonViewPage(final List<T> content, final Pageable pageable, final long total) {
    super(content, pageable, total);
  }

  public JsonViewPage(final List<T> content) {
    super(content);
  }

  public JsonViewPage(final Page<T> page, final Pageable pageable) {
    super(page.getContent(), pageable, page.getTotalElements());
  }

  public static <T> JsonViewPage<T> of(Page<T> page) {
    return new JsonViewPage<>(page.getContent(), page.getPageable(), page.getTotalPages());
  }

  @Override
  @JsonView(ClientConfigurationViews.ApplicationTerritory.class)
  @NonNull
  public List<T> getContent() {
    return super.getContent();
  }

  /**
   * Total pages.
   */
  @Override
  @JsonView(ClientConfigurationViews.ApplicationTerritory.class)
  @NonNull
  public int getTotalPages() {
    return super.getTotalPages();
  }

  /**
   * Returns the size of this page.
   */
  @Override
  @JsonView(ClientConfigurationViews.ApplicationTerritory.class)
  @NonNull
  public int getSize() {
    return getContent().size();
  }

  /**
   * Return the number of the slice
   */
  @Override
  @JsonView(ClientConfigurationViews.ApplicationTerritory.class)
  @NonNull
  public int getNumber() {
    return super.getNumber();
  }
}