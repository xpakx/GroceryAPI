package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.repository.CategoryRepository;
import com.example.groceryapi.error.NoSuchCategoryException;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Service
public class CategoryService
{
  private CategoryRepository categoryRepository;
  
  @Autowired
  public CategoryService(CategoryRepository categoryRepository)
  {
    this.categoryRepository = categoryRepository;
  }
  
  public List<Category> findAll()
  {
    return categoryRepository.findAll();
  }
  
  public Category findById(long i) throws NoSuchCategoryException
  {
    Optional<Category> category =  categoryRepository.findById(i);
    if(!category.isPresent()) 
    {
      throw new NoSuchCategoryException("Nie istnieje kategoria o podanym ID");
    }
    return category.get();
  }
  
  public void deleteCategory(long i) throws NoSuchCategoryException
  {
    Optional<Category> category =  categoryRepository.findById(i);
    if(!category.isPresent()) 
    {
      throw new NoSuchCategoryException("Nie istnieje kategoria o podanym ID");
    }
    categoryRepository.delete(category.get());
  }
  
  public void updateCategory(long i, Category category)
  {
    Optional<Category> oldCategory =  categoryRepository.findById(i);
    if(!oldCategory.isPresent()) 
    {
      throw new NoSuchCategoryException("Nie istnieje kategoria o podanym ID");
    }
    category.setId(i);
    categoryRepository.save(category);
  }
  
  public Category addCategory(Category category)
  {
    categoryRepository.save(category);
    return category;
  }
  
}
