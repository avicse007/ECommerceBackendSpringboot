package com.avi.eCommerce.security.config;

import com.avi.eCommerce.request.LoginRequest;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.response.JwtResponse;
import com.avi.eCommerce.security.config.jwt.JwtUtils;
import com.avi.eCommerce.service.cart.UserDetailService;
import com.avi.eCommerce.service.cart.UserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils  jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request){
        try {
            Authentication authentication = authenticationManager
                                                    .authenticate(
                                                            new UsernamePasswordAuthenticationToken
                                                                    (request.getEmail(),
                                                                            request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            JwtResponse response = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok().body(new ApiResponse("User logged in successfully", response));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Failed to login", e.getMessage()));
        }

    }
}
