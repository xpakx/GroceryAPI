package com.example.groceryapi.controller;

import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.service.ProductService;
import com.example.groceryapi.error.NoSuchProductException;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.NoSuchCategoryException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController
{
  private ProductService productService;
  
  @Autowired
  public void setService(ProductService productService)
  {
    this.productService = productService;
  }
  
  @GetMapping("/all")
  public List<Product> findAll()
  {
    return productService.findAll();
  }
  
  @GetMapping("/{id}")
  public Product findById(@PathVariable Long id) throws NoSuchProductException
  {
    return productService.findById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) throws NoSuchProductException //+odmowa dostępu
  {
    productService.deleteProduct(id);
  }
  
  @PutMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateById(@PathVariable Long id, @Valid @RequestBody Product product) throws NoSuchProductException //+odmowaDostępu +walidacja
  {
    productService.updateProduct(id, product);
  }
  
  @PostMapping
  public Product add(@Valid @RequestBody Product product) //+walidacja
  {
    return productService.addProduct(product);
  }
  
  @GetMapping("/byProducer/{producerName}")
  public List<Product> findAllByProducer(@PathVariable String producerName) throws NoSuchProducerException
  {
    return productService.findAllByProducer(producerName);
  }
  
  @GetMapping("/byCategory/{categoryName}")
  public List<Product> findAllByCategory(@PathVariable String categoryName) throws NoSuchCategoryException
  {
    return productService.findAllByCategory(categoryName);
  }
  
}
