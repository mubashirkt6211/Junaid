package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.CategoryNotFoundException;
import com.ecom.E_commerce.Exception.ItemNotFoundException;
import com.ecom.E_commerce.Model.Category;
import com.ecom.E_commerce.Model.Item;
import com.ecom.E_commerce.Repository.CategoryRepository;
import com.ecom.E_commerce.Request.CategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Override
    public Category createcategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return categoryRepository.save(category);
    }


    @Override
    public void deleteById(Long id) throws Exception {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    }

    @Override
    public List<Category> allCategory(Category category) {
        return categoryRepository.findAll();
    }


    @Override
    public Category updateCategory(Category updatedCategory, Long id) throws CategoryNotFoundException {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        existingCategory.setName(updatedCategory.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category not found with id: " + id)
        );
    }
}

