package com.ecom.E_commerce.Service;

import com.ecom.E_commerce.Exception.CategoryNotFoundException;
import com.ecom.E_commerce.Model.Category;
import com.ecom.E_commerce.Request.CategoryRequest;

import java.util.List;

public interface CategoryService {

    public Category createcategory(CategoryRequest request) throws Exception;

    public void deleteById(Long id) throws Exception;

    Category findByName(String name);

    List<Category> allCategory(Category category);

    Category updateCategory(Category category,Long id) throws CategoryNotFoundException;

    Category findById(Long id);





}