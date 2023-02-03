package nl.wilbertbongers.backend_eindopdracht.controller;


import nl.wilbertbongers.backend_eindopdracht.dto.AlbumRequestDto;
import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.repository.UserRepository;
import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long id) {

        Optional<UserDto> result = userService.getUserById(id);
        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User " +id+ " Not Found");
    }

    @PostMapping("/users")
    public ResponseEntity<String> postNewUser(@Valid @RequestBody UserDto userDto){
        String message = "New User created!";
        try {
            long user_id = userService.createUser(userDto);
            URI uri = URI.create(
                    ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/users/" + user_id).toUriString());
            return ResponseEntity.created(uri).body(message);
        } catch (Exception e) {
            message = "Creating User failed" + " Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        String message = "User: " + id + " deleted!";
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            message = "Deleting User failed" + " Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
