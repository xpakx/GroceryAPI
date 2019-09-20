package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.repository.ProducerRepository;
import com.example.groceryapi.repository.CategoryRepository;
import com.example.groceryapi.error.NoSuchProductException;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.NoSuchCategoryException;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ProductService
{
  private ProductRepository productRepository;
  private ProducerRepository producerRepository;
  private CategoryRepository categoryRepository;
  
  @Autowired
  public ProductService(ProductRepository productRepository, 
    ProducerRepository producerRepository, CategoryRepository categoryRepository)
  {
    this.productRepository = productRepository;
    this.producerRepository = producerRepository;
    this.categoryRepository = categoryRepository;
  }
  
  
  public List<Product> findAll()
  {
    return productRepository.findAll();
  }
  
  public Product findById(long i)
  {
    Optional<Product> product =  productRepository.findById(i);
    if(!product.isPresent()) 
    {
      throw new NoSuchProductException("Nie istnieje produkt o podanym ID");
    }
    return product.get();
  }
  
  public void deleteProduct(long i) 
  {
    Optional<Product> product =  productRepository.findById(i);
    if(!product.isPresent()) 
    {
      throw new NoSuchProductException("Nie istnieje produkt o podanym ID");
    }
    productRepository.delete(product.get());
  }
  
  public void updateProduct(long i, Product product)
  {
    Optional<Product> oldProduct =  productRepository.findById(i);
    if(!oldProduct.isPresent()) 
    {
      throw new NoSuchProductException("Nie istnieje produkt o podanym ID");
    }
    product.setId(i);
    productRepository.save(product);
  }
  
  public Product addProduct(Product product)
  {
    Producer producer = product.getProducer();
    Producer foundProducer = producerRepository.findByName(producer.getName());
    if(foundProducer == null)
    {
      producerRepository.save(producer);
    }
    else
    {
      product.setProducer(foundProducer);
    }
    product.setBought(0);
    productRepository.save(product);

    return product;
  }
  
  public List<Product> findAllByProducer(String producerName) throws NoSuchProducerException
  {
    Producer producer = producerRepository.findByName(producerName);
    if(producer == null)
    {
      throw new NoSuchProducerException("Nie istnieje podany producent");
    }
    return producer.getProducts();    
  }
  
  public List<Product> findAllByCategory(String categoryName) throws NoSuchCategoryException
  {
    Category category = categoryRepository.findByName(categoryName);
    if(category == null)
    {
      throw new NoSuchCategoryException("Nie istnieje podana kategoria");
    }
    return category.getProducts();    
  }
}
