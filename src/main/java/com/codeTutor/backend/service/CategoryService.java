package com.codeTutor.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeTutor.backend.model.Category;
import com.codeTutor.backend.repository.CategoryRepository;

/**
 * Service layer for Category entity.
 * Handles business logic for category operations.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves all categories.
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Finds a category by its ID.
     */
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }

    /**
     * Saves a new category.
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category.
     */
    public Optional<Category> update(UUID id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(updatedCategory.getName());
                    existingCategory.setDescription(updatedCategory.getDescription());
                    existingCategory.setIcon(updatedCategory.getIcon());
                    existingCategory.setOrderIndex(updatedCategory.getOrderIndex());
                    return categoryRepository.save(existingCategory);
                });
    }

    /**
     * Deletes a category by its ID.
     */
    public boolean delete(UUID id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
