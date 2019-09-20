package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.OrderStatus;
import com.example.groceryapi.entity.User;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TestOrderBuilder 
{
    @Builder(builderMethodName = "builder")
    public static Order newCategory(Long withId, BigDecimal withPrice, 
    Timestamp withDate, OrderStatus withOrderStatus, User withUser, 
    List<ProductOrder> withProductOrders) 
    {
      Order order = new Order();
      order.setId(withId);
      order.setPrice(withPrice); 
      order.setDate(withDate);
      order.setOrderStatus(withOrderStatus);
      order.setUser(withUser);
      order.setProductOrders(withProductOrders);
      return order;
    }
}
