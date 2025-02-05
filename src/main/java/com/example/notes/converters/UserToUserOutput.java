package com.example.notes.converters;

import com.example.notes.models.User;
import com.example.notes.models.dtos.UserOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserToUserOutput implements Converter<User, UserOutput> {

    @Override
    public UserOutput convert(User user) {
        return UserOutput.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
