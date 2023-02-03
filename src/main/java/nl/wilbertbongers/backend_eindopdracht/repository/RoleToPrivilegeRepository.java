package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.RoleToPrivilege;
import nl.wilbertbongers.backend_eindopdracht.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleToPrivilegeRepository extends CrudRepository<RoleToPrivilege, String> {
    List<RoleToPrivilege> findByRoleName(String roleName);
}
