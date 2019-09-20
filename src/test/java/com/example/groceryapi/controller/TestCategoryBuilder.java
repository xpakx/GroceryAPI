package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.entity.Product;
import java.util.List;

public class TestCategoryBuilder 
{
    @Builder(builderMethodName = "builder")
    public static Category newCategory(Long withId, String withName, List<Product> withProducts) 
    {
      Category category = new Category();
      category.setId(withId);
      category.setName(withName);
      category.setProducts(withProducts);
      return category;
    }
}
