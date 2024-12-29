package com.avi.eCommerce.service.user;

import com.avi.eCommerce.dto.UserDto;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.repository.UserRepository;
import com.avi.eCommerce.request.CreateUserRequest;
import com.avi.eCommerce.request.UpdateUserRequest;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id: "+id));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id).ifPresentOrElse(userRepository::delete, () -> {
            throw new ResourceNotFoundException("User not found with id: "+id);
        });
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request) {
        return convertToDto(userRepository.findById(request.getId()).map(user -> {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+request.getId())));
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        return Optional.of(request).filter(user-> !userRepository.existsByEmail(user.getEmail()))
                .map(user -> {
                    User newUser = new User();
                    newUser.setFirstName(user.getFirstName());
                    newUser.setLastName(user.getLastName());
                    newUser.setEmail(user.getEmail());
                    newUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    return convertToDto(userRepository.save(newUser));
                }).orElseThrow(() -> new ResourceNotFoundException("User already exists with email: "+request.getEmail()));
    }
    @Override
    public UserDto convertToDto(User user) {
        modelMapper.typeMap(User.class, UserDto.class).addMappings(mapper -> mapper.map(User::getCart,UserDto::setCartDto));
        UserDto userDto =  modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public User getAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepository.findByEmail(email);
        }catch (Exception e){
            throw new ResourceNotFoundException("User not found"+e.getMessage());
        }
    }
}
