package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.entity.User;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> 
{
  ShoppingCart findByUser(User user);
}


