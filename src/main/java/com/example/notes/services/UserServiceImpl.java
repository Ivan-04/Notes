package com.example.notes.services;

import com.example.notes.exceptions.DuplicateEntityException;
import com.example.notes.exceptions.EntityNotFoundException;
import com.example.notes.exceptions.UnauthorizedOperationException;
import com.example.notes.models.User;
import com.example.notes.models.dtos.Register;
import com.example.notes.models.dtos.UserOutput;
import com.example.notes.models.dtos.UserOutputId;
import com.example.notes.models.dtos.UserUpdate;
import com.example.notes.repositories.UserRepository;
import com.example.notes.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversionService conversionService;


    @Override
    public UserOutput findUserById(int id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User", id));

        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public UserOutput findUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email));

        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public User findUserEntityByEmail(String email){

        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public UserOutput findUserByFirstName(String firstName){
        User user = userRepository.findByFirstName(firstName).orElseThrow(
                () -> new EntityNotFoundException("User", "first name", firstName));

        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public UserOutput findUserByLastName(String lastName){
        User user = userRepository.findByLastName(lastName).orElseThrow(
                () -> new EntityNotFoundException("User", "last name", lastName));

        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public UserOutputId createUser(Register register) {

        String hashedPassword = passwordEncoder.encode(register.getPassword());

        User user = User.builder()
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .email(register.getEmail())
                .password(hashedPassword)
                .build();

        if(userRepository.existsByEmail(register.getEmail())){
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }

        userRepository.save(user);

        return conversionService.convert(user, UserOutputId.class);
    }

    @Override
    public UserUpdate editUser(User user, UserUpdate userUpdate){

        if(!passwordEncoder.matches(user.getPassword(), userUpdate.getPassword())){
            String hashedPassword = passwordEncoder.encode(userUpdate.getPassword());
            user.setPassword(hashedPassword);
        }

        user.setFirstName(userUpdate.getFirstName());
        user.setLastName(userUpdate.getLastName());
        user.setEmail(userUpdate.getEmail());

        userRepository.save(user);

        return conversionService.convert(user, UserUpdate.class);
    }

    @Override
    public void deleteUser(User user, int id){
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));

        if(!userToDelete.getEmail().equals(user.getEmail())){
            throw new UnauthorizedOperationException("You can delete only your account!");
        }

        userRepository.delete(user);
    }

}
