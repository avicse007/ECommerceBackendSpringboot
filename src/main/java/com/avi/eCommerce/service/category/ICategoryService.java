package com.avi.eCommerce.service.category;

import com.avi.eCommerce.model.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Category addCategory(Category category);
    void deleteCategoryById(Long id);
    Category updateCategory(Category category, Long categoryId);

}
