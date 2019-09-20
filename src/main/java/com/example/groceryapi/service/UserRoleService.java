package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.UserRole;
import com.example.groceryapi.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;
import java.util.Optional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserRoleService
{
  private UserRoleRepository userRoleRepository;
  
  @Autowired
  public UserRoleService(UserRoleRepository userRoleRepository)
  {
    this.userRoleRepository = userRoleRepository;
  }
  
  public UserRole getRole(String role)
  {
    UserRole userRole = userRoleRepository.findByRole(role);
    if(userRole == null) 
    {
      userRole = createRole(role);
    }
    return userRole;
  }
  
  public UserRole createRole(String role)
  {
    UserRole userRole = new UserRole();
    userRole.setRole(role);
    userRoleRepository.save(userRole);
    return userRole;
  }
  
}
