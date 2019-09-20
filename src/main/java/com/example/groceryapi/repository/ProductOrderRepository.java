package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.ProductOrder;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder,Long> 
{

}


