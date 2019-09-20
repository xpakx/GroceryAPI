package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.Product;
import java.util.List;
import java.math.BigDecimal;

public class TestProductOrderBuilder 
{
    @Builder(builderMethodName = "builder")
    public static ProductOrder newCategory(Long withId, Product withProduct, 
    BigDecimal withQuantity, BigDecimal withPrice) 
    {
      ProductOrder productOrder = new ProductOrder();
      productOrder.setId(withId);
      productOrder.setProduct(withProduct);
      productOrder.setPrice(withPrice); 
      productOrder.setQuantity(withQuantity);
      return productOrder;
    }
}
