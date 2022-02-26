package org.sitmun.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.sitmun.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "role")
@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends PagingAndSortingRepository<Role, Integer> {
}