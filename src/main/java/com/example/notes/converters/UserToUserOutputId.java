package com.example.notes.converters;

import com.example.notes.models.User;
import com.example.notes.models.dtos.UserOutputId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserToUserOutputId implements Converter<User, UserOutputId> {

    @Override
    public UserOutputId convert(User user) {
        return UserOutputId.builder()
                .userId(user.getId()).build();
    }
}
