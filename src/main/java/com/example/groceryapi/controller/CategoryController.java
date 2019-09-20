package com.example.groceryapi.controller;

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.service.CategoryService;
import com.example.groceryapi.error.NoSuchCategoryException;
import com.example.groceryapi.error.NoSuchProductException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController
{
  private CategoryService categoryService;
  
  @Autowired
  public void setService(CategoryService categoryService)
  {
    this.categoryService = categoryService;
  }
  
  @GetMapping("/all")
  public List<Category> findAll()
  {
    return categoryService.findAll();
  }
  
  @GetMapping("/{id}")
  public Category findById(@PathVariable Long id) throws NoSuchCategoryException
  {
    return categoryService.findById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) throws NoSuchCategoryException 
  {
    categoryService.deleteCategory(id);
  }
  
  @PutMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateById(@PathVariable Long id, @Valid @RequestBody Category category) throws NoSuchCategoryException
  {
    categoryService.updateCategory(id, category);
  }
  
  @PostMapping
  public Category add(@Valid @RequestBody Category category) 
  {
    return categoryService.addCategory(category);
  }  
}
