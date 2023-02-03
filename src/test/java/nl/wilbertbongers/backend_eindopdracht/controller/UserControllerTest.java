package nl.wilbertbongers.backend_eindopdracht.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.security.JwtRequestFilter;
import nl.wilbertbongers.backend_eindopdracht.service.JwtService;
import static org.mockito.BDDMockito.given;

import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtRequestFilter jwtRequestFilter;

    UserDto userDto;

    @BeforeEach
    void setUp() {
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
                "ADMIN");
   }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getUsers() throws Exception {

        given(userService.getAllUsers()).willReturn(List.of(userDto,userDto));

        ResultActions resultActions = mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailadress").value("test@wilbertbongers.nl"))
                .andExpect(jsonPath("$[0].password").value("$2a$10$UBCFfvqIysMB2BGXwRnHC.1AODFcvYSaW6pmFWzx3eNraq7F2fmg."))
                .andExpect(jsonPath("$[0].niceName").value( "Ed Kadet"))
                .andExpect(jsonPath("$[0].dateOfBirth").value( "2001-01-01"))
                .andExpect(jsonPath("$[0].street").value( "Rijstuin"))
                .andExpect(jsonPath("$[0].number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("$[0].postalCode").value( "3012CL"))
                .andExpect(jsonPath("$[0].city").value( "Rotterdam"))
                .andExpect(jsonPath("$[0].country").value( "Nederland"))
                .andExpect(jsonPath("$[0].telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("$[0].role").value( "ADMIN"));

    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    public void givenNotFoundWhenGetUserById() throws Exception {
        given(userService.getUserById(1L)).willThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User 1 Not Found"));

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById() throws Exception {

        given(userService.getUserById(1)).willReturn(Optional.ofNullable(userDto));

        ResultActions resultActions = mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("emailadress").value("test@wilbertbongers.nl"))
                .andExpect(jsonPath("password").value("$2a$10$UBCFfvqIysMB2BGXwRnHC.1AODFcvYSaW6pmFWzx3eNraq7F2fmg."))
                .andExpect(jsonPath("niceName").value( "Ed Kadet"))
                .andExpect(jsonPath("dateOfBirth").value( "2001-01-01"))
                .andExpect(jsonPath("street").value( "Rijstuin"))
                .andExpect(jsonPath("number").value( Integer.valueOf(10)))
                .andExpect(jsonPath("postalCode").value( "3012CL"))
                .andExpect(jsonPath("city").value( "Rotterdam"))
                .andExpect(jsonPath("country").value( "Nederland"))
                .andExpect(jsonPath("telephoneNumber").value( "010123456789"))
                .andExpect(jsonPath("role").value( "ADMIN"));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postNewUser() throws Exception {
        given(userService.createUser(userDto)).willReturn(1L);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void givenNotFoundWhenPostNewUser() throws Exception {
        given(userService.createUser(userDto)).willThrow(new RuntimeException("Creating User failed!"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void deleteUser() throws Exception {
        given(userService.deleteUser(2L)).willReturn(true);

        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User: 2 deleted!"));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void deleteUserThrowsException() throws Exception {
        given(userService.deleteUser(2L)).willThrow(new RuntimeException("Deleting User failed!"));

        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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