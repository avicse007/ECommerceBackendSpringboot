package com.avi.eCommerce.repository;

import com.avi.eCommerce.model.Category;
import com.avi.eCommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface   ProductRepository  extends JpaRepository<Product, Long> {

    List<Product> findByCategoryName(String categoryName);

    List<Product> findByCategoryNameAndBrand(String categoryName, String brand);

    List<Product> findByName(String name);

    List<Product> findByBrandAndName(String brand, String name);

    List<Product> findByBrand(String brand);

    Long countByBrandAndName(String brand, String name);
}
