package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.User;
import com.lukdut.monitoring.backend.repository.UserRepository;
import com.lukdut.monitoring.backend.rest.dto.ResponseDto;
import com.lukdut.monitoring.backend.rest.dto.UserDto;
import com.lukdut.monitoring.backend.security.Roles;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.lukdut.monitoring.backend.security.Roles.ROLE_PREFIX;

@RestController
@RequestMapping("/user")
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/all")
    @ApiOperation(value = "Find all users")
    public Collection<UserDto> all() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(user -> {
                    UserDto userDto = new UserDto();
                    userDto.setUsername(user.getUsername());
                    userDto.setRole(Roles.valueOf(user.getRole().replaceFirst(Roles.ROLE_PREFIX, "")));
                    return userDto;
                })
                .collect(Collectors.toList());
    }


    @PostMapping("/add")
    @ApiOperation(value = "Add new User",
            notes = "Will register new user with the specified name, password and role, returns 0 if failed or already exists")
    public synchronized long add(@RequestBody UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return 0;
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            return 0;
        }
        if (userDto.getRole() == null) {
            return 0;
        }

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return 0;
        }
        User newUser = new User();
        newUser.setRole(ROLE_PREFIX + userDto.getRole().name());
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Long userId = userRepository.save(newUser).getId();
        return userId;
    }

    @PutMapping("/setRole")
    @ApiOperation(value = "Update User role",
            notes = "Will update user role. Specified password would be ignored")
    public synchronized ResponseDto setRole(@RequestBody UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return ResponseDto.failResponse("Username is empty");
        }

        if (userDto.getRole() == null) {
            return ResponseDto.failResponse("Role is not specified");
        }

        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
        if (!optionalUser.isPresent()) {
            return ResponseDto.failResponse("User with name " + userDto.getUsername() + " does not exist");
        }

        if (userDto.getRole() != Roles.ADMIN && userRepository.countByRole(ROLE_PREFIX + Roles.ADMIN) == 1L) {
            return ResponseDto.failResponse("Can not change role for the only known Admin");
        }

        User user = optionalUser.get();
        user.setRole(ROLE_PREFIX + userDto.getRole().name());
        userRepository.save(user);

        return ResponseDto.okResponse();
    }

    @PutMapping("/setPassword")
    @ApiOperation(value = "Update User password",
            notes = "Will update user password. Specified role would be ignored")
    public synchronized ResponseDto setPassword(@RequestBody UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return ResponseDto.failResponse("Username is empty");
        }

        if (userDto.getPassword() == null) {
            return ResponseDto.failResponse("Password is not specified");
        }

        Optional<User> optionalUser = userRepository.findByUsername(userDto.getUsername());
        if (!optionalUser.isPresent()) {
            return ResponseDto.failResponse("User with name " + userDto.getUsername() + " does not exist");
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

        return ResponseDto.okResponse();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "!!! Deletes user with the specified username !!!")
    public synchronized ResponseDto delete(@RequestBody String username) {
        if (username == null || username.isEmpty()) {
            return ResponseDto.failResponse("Username is empty");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseDto.failResponse("User with name " + username + " does not exist");
        }

        if (optionalUser.get().getRole().equals(ROLE_PREFIX + Roles.ADMIN) &&
                userRepository.countByRole(ROLE_PREFIX + Roles.ADMIN) == 1L) {
            return ResponseDto.failResponse("Can not delete the only known Admin");
        }

        userRepository.deleteByUsername(username);

        return ResponseDto.okResponse();
    }
}
