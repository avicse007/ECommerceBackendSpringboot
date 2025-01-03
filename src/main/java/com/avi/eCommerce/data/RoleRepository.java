package com.avi.eCommerce.data;

import com.avi.eCommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String roleUser);
}
