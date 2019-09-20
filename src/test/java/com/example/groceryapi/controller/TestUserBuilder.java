package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.entity.UserRole;
import com.example.groceryapi.entity.UserDetails;
import com.example.groceryapi.entity.User;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TestUserBuilder 
{
    @Builder(builderMethodName = "builder")
    public static User newUser(Long withId, String withPassword, 
    String withEmail, List<Order> withOrders, ShoppingCart withShoppingCart, 
    UserDetails withUserDetails, List<UserRole> withRoles) 
    {
      User user = new User();
      user.setId(withId);
      user.setPassword(withPassword);
      user.setEmail(withEmail);
      user.setOrders(withOrders);
      user.setShoppingCart(withShoppingCart);
      user.setUserDetails(withUserDetails);
      user.setRoles(withRoles);
      return user;
    }
}
