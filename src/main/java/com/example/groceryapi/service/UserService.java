package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.UserRole;
import com.example.groceryapi.error.UserExistsException;
import com.example.groceryapi.error.UserNotLoggedException;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.groceryapi.repository.UserRepository;
import com.example.groceryapi.service.UserRoleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService
{
  private UserRepository userRepository;
  private UserRoleService userRoleService;
  
  @Autowired
  public UserService(UserRepository userRepository, UserRoleService userRoleService)
  {
    this.userRepository = userRepository;
    this.userRoleService = userRoleService;
  }
  
  public User registerUser(User user)
  {
    User userCheck = userRepository.findByEmail(user.getEmail());
    if(userCheck != null)
    {
      throw new UserExistsException("Użytkownik z podanym adresem email już istnieje");
    }
    
    if(user.getId() == null) 
    {
      PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      UserRole userRole = userRoleService.getRole("ROLE_USER");
      user.getRoles().add(userRole);
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    
    userRepository.save(user);
    
    return user;
  }
  
  public User getLoggedUser()
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findByEmail(authentication.getName());
    if(user == null)
    {
      throw new UserNotLoggedException("Niezalogowany");
    }
    return user;
  }
  
  public boolean isUserLogged()
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return !auth.getName().equals("anonymousUser");
  }
 
  public User updateUser(User user)
  {
    return userRepository.save(user);
  }
}
