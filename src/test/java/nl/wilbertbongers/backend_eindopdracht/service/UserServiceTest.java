package nl.wilbertbongers.backend_eindopdracht.service;

import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.model.*;
import nl.wilbertbongers.backend_eindopdracht.repository.*;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.util.ClassUtils;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    RoleToPrivilegeRepository roleToPrivilegeRepository;

    @InjectMocks
    UserService userService;

    @Captor
    ArgumentCaptor<User> argumentCaptor;

    User user1;
    User user2;
    UserDto userDto;
    Role role;

    RoleToPrivilege rToP1;
    RoleToPrivilege rToP2;
    Set<GrantedAuthority> authorities;
    @BeforeEach
    void setUp() {
        role = new Role(
            1l,
            "EDITOR",
            new ArrayList<User>(),
            new ArrayList<RoleToPrivilege>()
        );
        rToP1 = new RoleToPrivilege(1l, role, new Privilege(1l, "canRead", new ArrayList<RoleToPrivilege>()));
        rToP2 = new RoleToPrivilege(1l, role, new Privilege(2l, "canWrite", new ArrayList<RoleToPrivilege>()));

        authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("canRead"));
        authorities.add(new SimpleGrantedAuthority("ROLE_EDITOR"));
        authorities.add(new SimpleGrantedAuthority("canWrite"));

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
        user1 = new User(
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
            true,
            new ArrayList<>(),
            new ArrayList<>(),
            role
        );
        user2 = new User(
                1L,
                "tester@wilbertbongers.nl",
                "$2a$10$UBCFfvqIysMB2BGXwRnHC.1AODFcvYSaW6pmFWzx3eNraq7F2fmg.",
                "Tester 123",
                Date.valueOf("2001-02-02"),
                "Rijstuin",
                10,
                "3012CL",
                "Rotterdam",
                "Nederland",
                "010123456789",
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                role
        );
    }


    @Test
    void loadUserByUsername() {
        lenient().when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.ofNullable(user1));
        lenient().when(roleToPrivilegeRepository.findByRoleName("EDITOR")).thenReturn(List.of(rToP1,rToP2));

        CustomUserDetails ud = userService.loadUserByUsername(userDto.getEmailadress());
        User user = userRepository.findByUsername(user1.getUsername()).get();

        assertEquals(user.getUsername(),ud.getUsername());
        assertEquals(user.getPassword(),ud.getPassword());
        assertEquals(user.isActive(),ud.isEnabled());
        assertEquals(authorities,ud.getAuthorities());
    }

    @Test
    void createUser() throws Exception{
        lenient().when(userRepository.save(user1)).thenReturn(user1);
        lenient().when(roleRepository.findRoleByName("EDITOR")).thenReturn(Optional.ofNullable(role));
        userService.createUser(userDto);

        verify(userRepository, times(1)).save(argumentCaptor.capture());
        User user = argumentCaptor.getValue();

        assertEquals(user.getUsername(),userDto.getEmailadress());
        assertEquals(user.getStreet(),userDto.getStreet());
        assertEquals(user.getNumber(),userDto.getNumber());
        assertEquals(user.getCity(),userDto.getCity());
        assertEquals(user.getCountry(),userDto.getCountry());
        assertEquals(user.getPostalCode(),userDto.getPostalCode());
    }

    @Test
    void getUserById() {

        when(userRepository.findById(1l)).thenReturn(Optional.ofNullable(user1));

        UserDto newUserDto = userService.getUserById(1l).get();
        UserDto newUserDtofromUser = ClassUtils.createSingleUserDto(userRepository.findById(1l).get());

        assertEquals(newUserDtofromUser.getId(),newUserDto.getId());
        assertEquals(newUserDtofromUser.getEmailadress(),newUserDto.getEmailadress());
        assertEquals(newUserDtofromUser.getStreet(),newUserDto.getStreet());
        assertEquals(newUserDtofromUser.getNumber(),newUserDto.getNumber());
        assertEquals(newUserDtofromUser.getCity(),newUserDto.getCity());
        assertEquals(newUserDtofromUser.getCountry(),newUserDto.getCountry());
        assertEquals(newUserDtofromUser.getPostalCode(),newUserDto.getPostalCode());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1,user2));

        List<UserDto> list = userService.getAllUsers();
        List<UserDto> listFromDto = ClassUtils.createUserDtoList(userRepository.findAll());
        userRepository.findAll();

        assertEquals(list.get(0).getId(),listFromDto.get(0).getId());
        assertEquals(list.get(0).getEmailadress(),listFromDto.get(0).getEmailadress());
        assertEquals(list.get(0).getStreet(),listFromDto.get(0).getStreet());
        assertEquals(list.get(0).getNumber(),listFromDto.get(0).getNumber());
        assertEquals(list.get(0).getCity(),listFromDto.get(0).getCity());
        assertEquals(list.get(0).getCountry(),listFromDto.get(0).getCountry());
        assertEquals(list.get(0).getPostalCode(),listFromDto.get(0).getPostalCode());

        assertEquals(list.get(1).getId(),listFromDto.get(1).getId());
        assertEquals(list.get(1).getEmailadress(),listFromDto.get(1).getEmailadress());
        assertEquals(list.get(1).getStreet(),listFromDto.get(1).getStreet());
        assertEquals(list.get(1).getNumber(),listFromDto.get(1).getNumber());
        assertEquals(list.get(1).getCity(),listFromDto.get(1).getCity());
        assertEquals(list.get(1).getCountry(),listFromDto.get(1).getCountry());
        assertEquals(list.get(1).getPostalCode(),listFromDto.get(1).getPostalCode());
    }
}