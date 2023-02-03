package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String niceName;
    private Date dateOfBirth;
    private String street;
    private int number;
    private String postalCode;
    private String city;
    private String country;
    private String telephoneNumber;
    private boolean isActive;

    @OneToMany(mappedBy = "user")
    List<Claim> claims;

    @OneToMany(mappedBy = "user")
    List<Recording> recordingList;

    @OneToMany(mappedBy = "user")
    List<Quality> qualityList;

    @ManyToOne
    @JoinColumn(name="role_id", nullable=false)
    private Role role;

    public User() {}
    public User(
            long id,
            String username,
            String password,
            String niceName,
            Date dateOfBirth,
            String street,
            int number,
            String postalCode,
            String city,
            String country,
            String telephoneNumber,
            boolean isActive,
            List<Claim> claims,
            List<Recording> recordingList,
            Role role
            ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.niceName = niceName;
        this.dateOfBirth = dateOfBirth;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.telephoneNumber = telephoneNumber;
        this.isActive = isActive;
        this.claims = claims;
        this.recordingList = recordingList;
        this.role = role;
    }
}
