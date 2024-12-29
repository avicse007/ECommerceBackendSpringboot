package com.avi.eCommerce.repository;

import com.avi.eCommerce.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
     Optional<Image> findById(Long id);
     List<Image> findByProductId(Long productId);

}
