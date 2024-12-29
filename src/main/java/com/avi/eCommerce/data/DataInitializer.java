package com.avi.eCommerce.data;

import com.avi.eCommerce.model.Role;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Transactional
public class DataInitializer implements ApplicationListener<ApplicationEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        createDefaultRolesIfNotExists(Set.of("ROLE_USER", "ROLE_ADMIN"));
        createDefaultUserIfNotExists();
        createDefaultAdminIfNotExists();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    private void createDefaultUserIfNotExists() {
        Role role = roleRepository.findByName("ROLE_USER");
        for (int i = 1; i <= 5; i++) {
            String defaultEmail = "user" + i + "@email.com";
            if (!userRepository.existsByEmail(defaultEmail)) {
                User user = new User();
                user.setFirstName("User");
                user.setLastName(String.valueOf(i));
                user.setEmail(defaultEmail);
                user.setRoles(Set.of(role));
                user.setPassword(passwordEncoder.encode("password"));
                userRepository.save(user);
            }
        }
    }

    private void createDefaultAdminIfNotExists() {
        Role role = roleRepository.findByName("ROLE_ADMIN");
        for (int i = 1; i <= 2; i++) {
            String defaultEmail = "admin" + i + "@email.com";
            if (!userRepository.existsByEmail(defaultEmail)) {
                User user = new User();
                user.setFirstName("Admin");
                user.setLastName(String.valueOf(i));
                user.setEmail(defaultEmail);
                user.setRoles(Set.of(role));
                user.setPassword(passwordEncoder.encode("password"));
                userRepository.save(user);
            }
        }
    }

    private void createDefaultRolesIfNotExists(Set<String> roles) {
        System.out.println("===>>> Roles " + roles);
        roles.stream()
                .filter(role -> {
                    Role foundRole = roleRepository.findByName(role);
                    return foundRole == null;
                })
                .map(Role::new)
                .forEach(roleRepository::save);
    }
}
