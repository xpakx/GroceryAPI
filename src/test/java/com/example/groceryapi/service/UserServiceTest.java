package com.example.groceryapi.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.Assert.*;
import org.hamcrest.collection.IsCollectionWithSize;

import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import org.springframework.http.MediaType;
import java.nio.charset.Charset;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertThrows;


import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.UserRole;
import com.example.groceryapi.repository.UserRepository;
import com.example.groceryapi.repository.UserRoleRepository;
import com.example.groceryapi.error.UserNotLoggedException;
import com.example.groceryapi.error.UserExistsException;
import java.math.BigDecimal;
import java.util.Optional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class UserServiceTest
{
  @Mock
  private UserRepository repository;
  @Mock
  private UserRoleRepository userRoleRepository;

  @InjectMocks
  private UserService service;

  private User user;
  private User userToUpdate;
  private User userToAdd;

  @BeforeEach
  void setUp() 
  {
    MockitoAnnotations.initMocks(this);
    initProducts();
  }
  
  private void initProducts()
  {
    user = new User();
    user.setId(1L);
    user.setEmail("jan.kowalski@example.com");
    user.setPassword("test");
    
    userToUpdate = new User();
    userToUpdate.setId(1L);
    userToUpdate.setEmail("andrzej.nowak@example.com");
    
    
    userToAdd = new User();
    userToAdd.setEmail("jan.kowalski@example.com");
    userToAdd.setPassword("test");
    userToAdd.setRoles(new ArrayList<UserRole>());
  }

  @Test
  public void testUpdateUser() throws Exception
  {
    Mockito.when(repository.save(Mockito.any(User.class)))
      .thenReturn(user);
    
    User result = service.updateUser(userToAdd);
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(repository, Mockito.times(1)).save(userCaptor.capture());
    
    
    assertNotNull(result);
    assertThat(result.getEmail(), Matchers.is("jan.kowalski@example.com"));
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getEmail(), Matchers.is("jan.kowalski@example.com"));    
  }
  
  @Test
  public void testRegisterUserIfUserExists() throws Exception
  {
    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(user);
    
    assertThrows(UserExistsException.class, () -> service.registerUser(user));
    
    Mockito.verify(repository, Mockito.times(1)).findByEmail(Mockito.anyString());
  }
  
  
  @Test
  public void testRegisterUser() throws Exception
  {
    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(null);
    
    User result =  service.registerUser(userToAdd);
    
    Mockito.verify(repository, Mockito.times(1)).findByEmail(Mockito.anyString());
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(User.class));
    
    assertThat(result.getPassword(), Matchers.not(Matchers.is("test")));
    assertThat(result.getEmail(), Matchers.is("jan.kowalski@example.com"));
    
    
  }
  



}
