package nl.wilbertbongers.backend_eindopdracht.model;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "role_to_privileges")
public class RoleToPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Role role;

    @ManyToOne
    private Privilege privilege;

    public RoleToPrivilege() {}

    public RoleToPrivilege(long id, Role role, Privilege privilege) {
        this.id = id;
        this.role = role;
        this.privilege = privilege;
    }
}
