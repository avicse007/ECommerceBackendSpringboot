package com.avi.eCommerce.service.user;
import com.avi.eCommerce.dto.UserDto;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.request.CreateUserRequest;
import com.avi.eCommerce.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long id);
    void deleteUserById(Long id);
    UserDto updateUser(UpdateUserRequest request);
    UserDto createUser(CreateUserRequest request);

    UserDto convertToDto(User user);

    User getAuthenticatedUser();
}
