package com.example.groceryapi.controller;

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.service.ShoppingCartService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;
import java.util.Arrays;
import java.math.BigDecimal;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController
{
  private ShoppingCartService cartService;
  
  @Autowired
  public void setService(ShoppingCartService cartService)
  {
    this.cartService = cartService;
  }
  
  @PostMapping
  public ShoppingCart addProduct(@RequestBody Long id)
  {
    return cartService.addToCart(id);
  }
  
  @DeleteMapping("/{id}")
  public ShoppingCart deleteFromCart(@PathVariable Long id)
  {
    return cartService.deleteFromCart(id);
  }
  
  @GetMapping
  public List<ProductOrder> getProductsInCart()
  {
    return cartService.getProductsInCart();
  }
  
  @PutMapping("/{id}")
  public ShoppingCart updateProductInCart(@PathVariable Long id, @RequestBody BigDecimal newQuantity)
  {
    return cartService.updateProductInCart(id, newQuantity);
  }
    

  
}
