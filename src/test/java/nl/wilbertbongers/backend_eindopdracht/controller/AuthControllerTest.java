package nl.wilbertbongers.backend_eindopdracht.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.wilbertbongers.backend_eindopdracht.security.JwtRequestFilter;
import nl.wilbertbongers.backend_eindopdracht.service.JwtService;
import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

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

    @MockBean
    private AuthenticationManager authManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    void signIn() {
    }
}