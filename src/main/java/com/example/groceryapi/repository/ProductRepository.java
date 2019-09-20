package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> 
{

}


