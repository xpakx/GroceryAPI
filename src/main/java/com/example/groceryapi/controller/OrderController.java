package com.example.groceryapi.controller;

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.OrderStatus;
import com.example.groceryapi.service.OrderService;
import com.example.groceryapi.error.NoSuchOrderException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/order")
public class OrderController
{
  private OrderService orderService;
  
  @Autowired
  public void setService(OrderService orderService)
  {
    this.orderService = orderService;
  }
  
  @GetMapping("/all")
  public List<Order> findAll()
  {
    return orderService.findAll();
  }
  
  @GetMapping("/{id}")
  public Order findById(@PathVariable Long id) throws NoSuchOrderException
  {
    return orderService.findById(id);
  }
    
  @PutMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateById(@PathVariable Long id, @Valid @RequestBody Order order) throws NoSuchOrderException
  {
    orderService.updateOrder(id, order);
  }
  
  @PostMapping
  public Order saveOrder() 
  {
    return orderService.saveOrder();
  }
  
  @GetMapping(value = "/getStatuses")
  public List<OrderStatus> getStatuses()
  {
    return Arrays.asList(OrderStatus.values());
  }
  
}
