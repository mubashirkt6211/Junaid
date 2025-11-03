package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Exception.CategoryNotFoundException;
import com.ecom.E_commerce.Model.Category;
import com.ecom.E_commerce.Request.CategoryRequest;
import com.ecom.E_commerce.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest request) throws Exception {
        Category category = categoryService.createcategory(request);
        return new ResponseEntity<>(category,HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> allCategory(Category category){
        List<Category> allcategory = categoryService.allCategory(category);
        return ResponseEntity.ok(allcategory);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) throws Exception {
        categoryService.deleteById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody Category updatedCategory) throws CategoryNotFoundException {
        Category category = categoryService.updateCategory(updatedCategory, id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category updated successfully");
        response.put("category", category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.findByName(name);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category found successfully");
        response.put("category", category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable("id") Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

}
