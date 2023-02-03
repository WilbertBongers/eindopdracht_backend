package nl.wilbertbongers.backend_eindopdracht.security;

import nl.wilbertbongers.backend_eindopdracht.repository.RoleRepository;
import nl.wilbertbongers.backend_eindopdracht.repository.RoleToPrivilegeRepository;
import nl.wilbertbongers.backend_eindopdracht.repository.UserRepository;
import nl.wilbertbongers.backend_eindopdracht.service.JwtService;
import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleToPrivilegeRepository roleToPrivilegeRepository;
    private PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtService service,
            UserRepository userRepos,
            RoleToPrivilegeRepository roleToPrivilegeRepository,
            RoleRepository roleRepository
            ) {
        this.jwtService = service;
        this.userRepository = userRepos;
        this.roleRepository = roleRepository;
        this.roleToPrivilegeRepository = roleToPrivilegeRepository;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder encoder, UserDetailsService udService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(udService)
                .passwordEncoder(encoder)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth").permitAll()
                .antMatchers(HttpMethod.POST, "/users/").access("hasAuthority('canManageUser')")
                .antMatchers(HttpMethod.POST, "/requests").access("hasAuthority('canAddRequest')")
                .antMatchers(HttpMethod.POST, "/requests/claim").access("hasAuthority('canRecord')")
                .antMatchers(HttpMethod.POST, "/requests/batch-import").access("hasAuthority('canManageUser')")
                .antMatchers(HttpMethod.POST, "/requests/{id}/recording").access("hasAuthority('canRecord')")
                .antMatchers(HttpMethod.POST, "/requests/{id}/quality").access("hasAuthority('canQcRecording')")
                .antMatchers("/requests/{id}/claim/").access("hasAuthority('canRecord')")
                .antMatchers("/requests/state/{state}").access("hasAuthority('canAddRequest')")
                .antMatchers("/requests/worklist").access("hasAuthority('canRecord')")
                .antMatchers("/requests/qualitylist").access("hasAuthority('canQcRecording')")
                .antMatchers("/requests/claimed").access("hasAuthority('canGetClaimReport')")
                .antMatchers("/requests").access("hasAuthority('canGetUserReport')")
                .antMatchers("/users/{id}").access("hasAuthority('canManageUser')")
                .antMatchers("/users").access("hasAuthority('canManageUser')")
                .antMatchers(HttpMethod.PUT,"/users/{id}").access("hasAuthority('canManageUser')")
                .antMatchers(HttpMethod.PUT,"/requests/{id}").access("hasAuthority('canAlterRequest')")
                .antMatchers(HttpMethod.DELETE,"/users/{id}").access("hasAuthority('canManageUser')")
                .antMatchers(HttpMethod.DELETE,"/requests/{id}/claim").access("hasAuthority('canAlterRequest')")
                .and()
                .addFilterBefore(new JwtRequestFilter(jwtService, new UserService(this.userRepository , this.roleToPrivilegeRepository, this.roleRepository, this.passwordEncoder)), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
