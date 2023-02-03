package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;
@Getter
@Setter
public class UserDto {

    long id;

    @NotEmpty @Email
    public String emailadress;
    @Size(min = 6, message = "Invalid Name: Must be of 3 - 30 characters")
    public String password;
    private String niceName;
    private Date dateOfBirth;
    private String street;
    private int number;
    private String postalCode;
    private String city;
    private String country;
    @NotBlank(message = "Invalid Phone number: Empty number")
    @NotNull(message = "Invalid Phone number: Number is NULL")
    @Pattern(regexp = "^\\d{12}$", message = "Invalid phone number")
    private String telephoneNumber;
    @NotEmpty
    public String role;

    public UserDto() {
    }
    public UserDto (
            long id,
            String emailadress,
            String password,
            String niceName,
            Date dateOfBirth,
            String street,
            int number,
            String postalCode,
            String city,
            String country,
            String telephoneNumber,
            String role
            ) {
        this.id = id;
        this.emailadress = emailadress;
        this.password = password;
        this.niceName = niceName;
        this.dateOfBirth = dateOfBirth;
        this.street = street;
        this.number = number;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.telephoneNumber = telephoneNumber;
        this.role = role;
    }
}
