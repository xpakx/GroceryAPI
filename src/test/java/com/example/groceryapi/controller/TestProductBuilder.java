package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import java.math.BigDecimal;

public class TestProductBuilder 
{
    @Builder(builderMethodName = "builder")
    public static Product newProduct(Long withId, String withName, 
      String withDescription, BigDecimal withPrice, BigDecimal withOriginalPrice, 
      Producer withProducer, Integer withInStock, Integer withBought, 
      String withPicture) 
    {
      Product product = new Product();
      product.setId(withId);
      product.setName(withName);
      product.setDescription(withDescription);
      product.setPrice(withPrice);
      product.setOriginalPrice(withOriginalPrice); 
      product.setProducer(withProducer);
      product.setInStock(withInStock);
      product.setBought(withBought); 
      product.setPicture(withPicture);
      return product;
    }
    
}
