package org.sitmun.plugin.core.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.sitmun.plugin.core.domain.QueryTask;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

@Tag(name = "query task")
@RepositoryRestResource(collectionResourceRel = "query-tasks", path = "query-tasks")
public interface QueryTaskRepository extends PagingAndSortingRepository<QueryTask, Integer> {

  @Override
  @PreAuthorize("hasPermission(#entity, 'administration') or hasPermission(#entity, 'write')")
  @NonNull
  <S extends QueryTask> S save(@P("entity") @NonNull S entity);

  @Override
  @PreAuthorize("hasPermission(#entity, 'administration') or hasPermission(#entity,  'delete')")
  void delete(@P("entity") @NonNull QueryTask entity);

  @Override
  @PreAuthorize("hasPermission(#entityId, 'org.sitmun.plugin.core.domain.QueryTask','administration') or hasPermission(#entityId, 'org.sitmun.plugin.core.domain.Connection', 'delete')")
  void deleteById(@P("entityId") @NonNull Integer entityId);

  @Override
  @PostFilter("hasPermission(filterObject, 'administration') or hasPermission(filterObject, 'read')")
  @NonNull
  Iterable<QueryTask> findAll();

  @Override
  @PreAuthorize("hasPermission(#entityId, 'org.sitmun.plugin.core.domain.QueryTask','administration') or hasPermission(#entityId, 'org.sitmun.plugin.core.domain.Connection', 'read')")
  @NonNull
  Optional<QueryTask> findById(@P("entityId") @NonNull Integer entityId);

}