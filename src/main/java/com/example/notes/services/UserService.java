package com.example.notes.services;

import com.example.notes.models.User;
import com.example.notes.models.dtos.Register;
import com.example.notes.models.dtos.UserOutput;
import com.example.notes.models.dtos.UserOutputId;
import com.example.notes.models.dtos.UserUpdate;

public interface UserService {
    UserOutput findUserById(int id);

    UserOutput findUserByEmail(String email);

    User findUserEntityByEmail(String email);

    UserOutput findUserByFirstName(String firstName);

    UserOutput findUserByLastName(String lastName);

    UserOutputId createUser(Register register);

    UserUpdate editUser(User user, UserUpdate userUpdate);

    void deleteUser(User user, int id);
}
