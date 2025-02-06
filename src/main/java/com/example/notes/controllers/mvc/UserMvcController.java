package com.example.notes.controllers.mvc;

import com.example.notes.exceptions.AuthenticationFailureException;
import com.example.notes.exceptions.AuthorizationException;
import com.example.notes.exceptions.DuplicateEntityException;
import com.example.notes.helpers.AuthenticationHelper;
import com.example.notes.models.User;
import com.example.notes.models.dtos.UserOutput;
import com.example.notes.models.dtos.UserUpdate;
import com.example.notes.services.contracts.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserMvcController {

    private final ConversionService conversionService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }


    @GetMapping("/{id}")
    public String showSingleUser(@PathVariable int id, Model model) {
        try {
            UserOutput user = userService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("id", id);
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/me")
    public String showSingleUser(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            model.addAttribute("user", conversionService.convert(user, UserOutput.class));
            model.addAttribute("userId", user.getId());
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


    @GetMapping("/{id}/edit")
    public String showEditUserPage(@PathVariable int id, Model model, HttpSession session) {
        User user1;
        try {
            user1 = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/login";
        }

        try {
            UserUpdate userUpdate = conversionService.convert(user1, UserUpdate.class);
            model.addAttribute("user", userUpdate);
            model.addAttribute("id", id);
            return "UserUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable int id, @Valid @ModelAttribute("user") UserUpdate userDto,
                             BindingResult bindingResult, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("user", userService.findUserById(id));
            return "UserUpdateView";
        }

        try {
            model.addAttribute("user", userService.findUserEntityById(id));
            model.addAttribute("id", id);
            model.addAttribute("userToUpdate", userDto);

            userService.editUser(user, userDto);
            return "redirect:/users/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("name", "duplicate_post", e.getMessage());
            return "UserView";
        } catch (AuthorizationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            userService.deleteUser(user, id);
            return "redirect:/auth/logout";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

}
