package com.example.notes.converters;

import com.example.notes.models.User;
import com.example.notes.models.dtos.UserUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserToUserUpdate implements Converter<User, UserUpdate> {

    @Override
    public UserUpdate convert(User user) {
        return UserUpdate.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
