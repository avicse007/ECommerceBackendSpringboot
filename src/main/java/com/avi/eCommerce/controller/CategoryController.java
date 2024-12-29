package com.avi.eCommerce.controller;

import com.avi.eCommerce.model.Category;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.category.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("${api.prefix}/category")
public class CategoryController {
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> getAllCategories() {
        try{
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok().body(new ApiResponse("Categories fetched successfully", categories));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch categories", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category category) {
        try{
            Category newCategory = categoryService.addCategory(category);
            return ResponseEntity.ok().body(new ApiResponse("Category added successfully", newCategory));
        }catch (Exception e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse("Failed to add category", e.getMessage()));
        }
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try{
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok().body(new ApiResponse("Category fetched successfully", category));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch category", e.getMessage()));
        }
    }

    @GetMapping("/category/name/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        try{
            Category category = categoryService.getCategoryByName(name);
            return ResponseEntity.ok().body(new ApiResponse("Category fetched successfully", category));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch category", e.getMessage()));
        }
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try{
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok().body(new ApiResponse("Category deleted successfully", null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to delete category", e.getMessage()));
        }
    }
}
