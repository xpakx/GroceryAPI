package com.example.groceryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


import java.math.BigDecimal;

@SpringBootApplication
public class Application implements CommandLineRunner
{

  @Autowired
  private ProductRepository productRepository;
    
	public static void main(String[] args) 
  {
		SpringApplication.run(Application.class, args);
	}
  
  @Override
  public void run(String...args) throws Exception
  {
   /* productRepository.deleteAll();
    //save some products
   // productRepository.save(Product.builder().name("tomato").price(new BigDecimal(5.99)).build());
    //productRepository.save(Product.builder().name("orange").price(new BigDecimal(6.99)).build());
    //fetch all product:
    System.out.println("fetchingproducts...");
    System.out.println("-------------------------");
    for(Product product: productRepository.findAll())
    {
      System.out.println(product);
    }*/
    }
}
