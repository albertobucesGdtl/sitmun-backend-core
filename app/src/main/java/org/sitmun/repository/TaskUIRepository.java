package org.sitmun.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.sitmun.domain.TaskUI;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "task ui")
@RepositoryRestResource(collectionResourceRel = "task-uis", path = "task-uis")
public interface TaskUIRepository extends PagingAndSortingRepository<TaskUI, Integer> {
}