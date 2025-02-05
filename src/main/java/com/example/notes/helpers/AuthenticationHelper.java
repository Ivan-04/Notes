package com.example.notes.helpers;

import com.example.notes.exceptions.AuthenticationFailureException;
import com.example.notes.exceptions.AuthorizationException;
import com.example.notes.models.User;
import com.example.notes.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationHelper {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String WRONG_EMAIL_OR_PASSWORD = "Wrong email or password";

    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
        String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        String email = getEmail(userInfo);
        String password = getPassword(userInfo);
        User user = userService.findUserEntityByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthorizationException(WRONG_EMAIL_OR_PASSWORD);
        }

        return user;
    }

    private String getEmail(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(0, firstSpace);
    }

    private String getPassword(String userInfo) {
        int firstSpace = userInfo.indexOf(" ");
        if (firstSpace == -1) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(firstSpace + 1);
    }

    public User tryGetUser(HttpSession session) {
        String currentUser = (String) session.getAttribute("currentUser");

        if (currentUser == null) {
            throw new AuthenticationFailureException("No user logged in.");
        }

        return userService.findUserEntityByEmail(currentUser);
    }

    public User verifyAuthentication(String email, String password) {
        try {
            User user = userService.findUserEntityByEmail(email);
            if (user == null) {
                throw new EntityNotFoundException("User not found.");
            }

            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new AuthenticationFailureException(WRONG_EMAIL_OR_PASSWORD);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(WRONG_EMAIL_OR_PASSWORD);
        } catch (AuthenticationFailureException e) {
            throw new AuthenticationFailureException(WRONG_EMAIL_OR_PASSWORD);
        }
    }
}
