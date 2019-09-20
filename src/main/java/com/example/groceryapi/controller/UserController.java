package com.example.groceryapi.controller;

import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.UserDetails;
import com.example.groceryapi.service.UserService;
import com.example.groceryapi.validation.Register;
import com.example.groceryapi.validation.ConfirmOrder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;
import java.util.Arrays;
import java.math.BigDecimal;

@RestController
@RequestMapping("/user")
public class UserController
{
  private UserService userService;
  
  @Autowired
  public void setService(UserService userService)
  {
    this.userService = userService;
  }
  
  @PostMapping
  public User register(@Validated(Register.class) @RequestBody User user)
  {
    return userService.registerUser(user);
  }
  
  @GetMapping
  public User loggedUser()
  {
    return userService.getLoggedUser();
  }
  
  @PutMapping
  public User updateDetails(@Validated(ConfirmOrder.class) @RequestBody User user)
  {
    return userService.updateUser(user);
  }
}
