package nl.wilbertbongers.backend_eindopdracht.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.model.Role;
import nl.wilbertbongers.backend_eindopdracht.model.User;
import nl.wilbertbongers.backend_eindopdracht.repository.UserRepository;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.service.JwtService;
import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    UserDto userDto;
    User user;
    Role role;
    String accessToken;

    @BeforeEach
    public void setUp() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        CustomUserDetails ud =  new CustomUserDetails(
                "admin user",
                "123test",
                true,
                authorities
        );

        accessToken = jwtService.generateToken(ud);

        userDto = new UserDto(
            1L,
            "test@wilbertbongers.nl",
            "$2a$10$UBCFfvqIysMB2BGXwRnHC.1AODFcvYSaW6pmFWzx3eNraq7F2fmg.",
            "Ed Kadet",
            Date.valueOf("2001-01-01"),
            "Rijstuin",
            10,
            "3012CL",
            "Rotterdam",
            "Nederland",
            "010123456789",
            "EDITOR"
        );
    }

    @Test

    void getUsers() throws Exception{
        ResultActions resultActions = mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailadress").value("admin@wilbertbongers.nl"))
                .andExpect(jsonPath("$[0].password").value("**************"))
                .andExpect(jsonPath("$[0].niceName").value( "admin user"))
                .andExpect(jsonPath("$[0].street").value( "wegisweg"))
                .andExpect(jsonPath("$[0].number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("$[0].postalCode").value( "1234AA"))
                .andExpect(jsonPath("$[0].city").value( "Rotterdam"))
                .andExpect(jsonPath("$[0].country").value( "Nederland"))
                .andExpect(jsonPath("$[0].telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("$[0].role").value( "ADMIN"))
                .andExpect(jsonPath("$[1].emailadress").value("editor@wilbertbongers.nl"))
                .andExpect(jsonPath("$[1].password").value("**************"))
                .andExpect(jsonPath("$[1].niceName").value( "editor user"))
                .andExpect(jsonPath("$[1].street").value( "wegisweg"))
                .andExpect(jsonPath("$[1].number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("$[1].postalCode").value( "1234AA"))
                .andExpect(jsonPath("$[1].city").value( "Rotterdam"))
                .andExpect(jsonPath("$[1].country").value( "Nederland"))
                .andExpect(jsonPath("$[1].telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("$[1].role").value( "EDITOR"))
                .andExpect(jsonPath("$[2].emailadress").value("engineer@wilbertbongers.nl"))
                .andExpect(jsonPath("$[2].password").value("**************"))
                .andExpect(jsonPath("$[2].niceName").value( "engineer user"))
                .andExpect(jsonPath("$[2].street").value( "wegisweg"))
                .andExpect(jsonPath("$[2].number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("$[2].postalCode").value( "1234AA"))
                .andExpect(jsonPath("$[2].city").value( "Rotterdam"))
                .andExpect(jsonPath("$[2].country").value( "Nederland"))
                .andExpect(jsonPath("$[2].telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("$[2].role").value( "ENGINEER"));
    }
    @Test
    void getUserById() throws Exception{
        ResultActions resultActions = mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailadress").value("admin@wilbertbongers.nl"))
                .andExpect(jsonPath("$.password").value("**************"))
                .andExpect(jsonPath("$.niceName").value( "admin user"))
                .andExpect(jsonPath("$.street").value( "wegisweg"))
                .andExpect(jsonPath("$.number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("$.postalCode").value( "1234AA"))
                .andExpect(jsonPath("$.city").value( "Rotterdam"))
                .andExpect(jsonPath("$.country").value( "Nederland"))
                .andExpect(jsonPath("$.telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("$.role").value( "ADMIN"));
    }
    @Test
    void postNewUser() throws Exception{
        mockMvc.perform(post("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    void deleteUser() throws Exception{
        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User: 2 deleted!"));
    }
    public static String asJsonString(final UserDto obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
