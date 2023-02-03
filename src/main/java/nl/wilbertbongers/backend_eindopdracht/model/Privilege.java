package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "privileges")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="privilege_id")
    private long id;

    private String privilegeName;

    @OneToMany(mappedBy = "privilege")
    private List<RoleToPrivilege> roleToPrivileges;

    public Privilege() {}

    public Privilege(long id, String privilegeName, List<RoleToPrivilege> roleToPrivileges) {
        this.id = id;
        this.privilegeName = privilegeName;
        this.roleToPrivileges = roleToPrivileges;
    }
}
