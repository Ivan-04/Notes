package com.example.notes.controllers.mvc;

import com.example.notes.exceptions.AuthenticationFailureException;
import com.example.notes.exceptions.DuplicateEntityException;
import com.example.notes.exceptions.EntityNotFoundException;
import com.example.notes.helpers.AuthenticationHelper;
import com.example.notes.models.User;
import com.example.notes.models.dtos.Login;
import com.example.notes.models.dtos.Register;
import com.example.notes.models.dtos.UserOutputId;
import com.example.notes.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final ConversionService conversionService;

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "Login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") Login loginDto,
                              BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "Login";
        }

        try {
            User user = authenticationHelper.verifyAuthentication(loginDto.getEmail(), loginDto.getPassword());
            session.setAttribute("currentUser", loginDto.getEmail());
            return "redirect:/";
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("email", "auth_error", e.getMessage());
            return "Login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("register", new Register());
        return "Register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") Register registerDto, BindingResult bindingResult,
                                 Model model) {
        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "password_error", "Password confirmation should match password");
        }

        if (userService.existsByEmail(registerDto.getEmail())) {
            bindingResult.rejectValue("email", "email_error", "Email is already in use");
        }

        if (bindingResult.hasErrors()) {
            return "Register";
        }

        try {
            UserOutputId userOutputId = userService.createUser(registerDto);
            User user = userService.findUserEntityById(userOutputId.getUserId());
            model.addAttribute("user", user);
            return "redirect:/auth/login";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "Register";
        }
    }

}
