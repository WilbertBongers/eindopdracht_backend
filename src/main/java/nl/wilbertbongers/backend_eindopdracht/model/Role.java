package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private long id;
    @Column(name="role_name")
    private String name;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    @OneToMany(mappedBy = "role")
    private List<RoleToPrivilege> roleToPrivileges;

    public Role () {}
    public Role(long id, String name, ArrayList<User> users, ArrayList<RoleToPrivilege> roleToPrivileges) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.roleToPrivileges = roleToPrivileges;
    }
}
