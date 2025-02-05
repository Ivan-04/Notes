package com.example.notes.controllers;

import com.example.notes.helpers.AuthenticationHelper;
import com.example.notes.models.User;
import com.example.notes.models.dtos.Register;
import com.example.notes.models.dtos.UserOutput;
import com.example.notes.models.dtos.UserOutputId;
import com.example.notes.models.dtos.UserUpdate;
import com.example.notes.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;


    @GetMapping("/{id}")
    public ResponseEntity<UserOutput> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/firstName")
    public ResponseEntity<UserOutput> getUserByFirstName(@RequestParam String firstName) {
        return ResponseEntity.ok(userService.findUserByFirstName(firstName));
    }

    @GetMapping("/lastName")
    public ResponseEntity<UserOutput> getUserByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(userService.findUserByLastName(lastName));
    }

    @GetMapping("/email")
    public ResponseEntity<UserOutput> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserOutputId> createUser(@Valid @RequestBody Register register) {
        return ResponseEntity.ok(userService.createUser(register));
    }

    @PutMapping
    public ResponseEntity<UserUpdate> editUser(@RequestHeader HttpHeaders headers, @Valid @RequestBody UserUpdate userUpdate) {
        User user = authenticationHelper.tryGetUser(headers);
        return ResponseEntity.ok(userService.editUser(user, userUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUser(@RequestHeader HttpHeaders headers, @Valid @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        userService.deleteUser(user, id);
        return ResponseEntity.ok("User deleted successfully!");
    }
}
