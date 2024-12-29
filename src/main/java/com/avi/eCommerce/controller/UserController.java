package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.UserDto;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.request.CreateUserRequest;
import com.avi.eCommerce.request.UpdateUserRequest;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.user.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId){
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertToDto(user);
            return ResponseEntity.ok().body(new ApiResponse("User fetched successfully", userDto));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                           .body(new ApiResponse("Failed to fetch user", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request){
        try {
            UserDto user = userService.createUser(request);
            return ResponseEntity.ok().body(new ApiResponse("User created successfully", user));
        } catch (Exception e) {
            return ResponseEntity.status(CONFLICT)
                           .body(new ApiResponse("Failed to create user", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UpdateUserRequest request){
        try {
            UserDto user = userService.updateUser(request);
            return ResponseEntity.ok().body(new ApiResponse("User updated successfully", user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                           .body(new ApiResponse("Failed to update user", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok().body(new ApiResponse("User deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                           .body(new ApiResponse("Failed to delete user", e.getMessage()));
        }
    }
}
