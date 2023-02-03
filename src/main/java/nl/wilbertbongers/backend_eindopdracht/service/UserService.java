package nl.wilbertbongers.backend_eindopdracht.service;

import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.model.Role;
import nl.wilbertbongers.backend_eindopdracht.model.RoleToPrivilege;
import nl.wilbertbongers.backend_eindopdracht.model.User;
import nl.wilbertbongers.backend_eindopdracht.repository.RoleRepository;
import nl.wilbertbongers.backend_eindopdracht.repository.RoleToPrivilegeRepository;
import nl.wilbertbongers.backend_eindopdracht.repository.UserRepository;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.util.ClassUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleToPrivilegeRepository roleToPrivilegeRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(
            UserRepository userRepository,
            RoleToPrivilegeRepository roleToPrivilegeRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleToPrivilegeRepository = roleToPrivilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public CustomUserDetails loadUserByUsername(String email) {

        Optional<User> userOpt = userRepository.findByUsername(email);

        if (userOpt.isPresent()) {

            User user = userOpt.get();
            Set<GrantedAuthority> authorities = new HashSet<>();
            String role = user.getRole().getName();

            List<RoleToPrivilege> privilegeList = roleToPrivilegeRepository.findByRoleName(role);

            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            for (RoleToPrivilege privilege : privilegeList) {
                authorities.add(new SimpleGrantedAuthority(privilege.getPrivilege().getPrivilegeName()));
            }

            return new CustomUserDetails(user.getUsername(), user.getPassword(), user.isActive(), authorities);
        }
        else {
            throw new UsernameNotFoundException(userOpt.get().getUsername());
        }
    }
    public Optional<UserDto> getUserById(long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            return Optional.of(ClassUtils.createSingleUserDto(userOpt.get()));
        } else {
            return Optional.empty();
        }
    }
    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return ClassUtils.createUserDtoList(userList);
    }
    public long createUser(UserDto userDto) throws Exception{

        Optional<Role> roleOpt = roleRepository.findRoleByName(userDto.getRole().toUpperCase());
        if (roleOpt.isPresent() && !roleOpt.get().getName().equals("ADMIN")) {
            try {
                User user = ClassUtils.createSingleUser(userDto);
                user.setRole(roleOpt.get());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                userRepository.save(user);
                return user.getId();
            } catch (Exception e) {
                throw new RuntimeException("Could not save User");
            }
        } else {
            String message = "";
            if (roleOpt.isEmpty()) {
                message = "Role does not exist!";
            } else {
                message = "Not allowed to add user with Role ADMIN";
            }
            throw new RuntimeException(message);
        }
    }
    public boolean deleteUser(long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Deleting user failed!");
        }
    }
}
