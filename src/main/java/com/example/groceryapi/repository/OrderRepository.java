package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.OrderStatus;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> 
{
  List<Order> findByOrderStatus(OrderStatus orderStatus);
}


