package com.avi.eCommerce.service.category;

import com.avi.eCommerce.exceptions.CategoryAlreadyException;
import com.avi.eCommerce.exceptions.CategoryNotFoundException;
import com.avi.eCommerce.model.Category;
import com.avi.eCommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService  implements ICategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                       .orElseThrow(()-> new CategoryNotFoundException("No Category found with id"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    @Override
    public Category addCategory(Category category) {
        return  Optional.of(category).filter(c->!categoryRepository.existsByName(c.getName()))
                        .map(categoryRepository::save)
                        .orElseThrow(()-> new CategoryAlreadyException("Category already exists"));
    }


    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete,()->{
            throw new CategoryNotFoundException("No Category found with id");
        });
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        return Optional.of(categoryRepository.findById(categoryId))
                .map(category1 -> {
                    category.setId(categoryId);
                    return categoryRepository.save(category);
                }).orElseThrow(()-> new CategoryNotFoundException("No Category found with id"));
    }
}
