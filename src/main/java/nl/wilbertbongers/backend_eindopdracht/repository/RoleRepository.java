package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findRoleByName(String roleName);
}

