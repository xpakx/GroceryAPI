package com.example.groceryapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.Assert.*;

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

import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.UserDetails;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.service.UserService;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.UserExistsException;
import com.example.groceryapi.error.UserNotLoggedException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class UserControllerTest 
{
  @Mock
  private UserService service;

  @InjectMocks
  private UserController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private User toAdd;
  private User toAddInvalid;
  private User user;

  @BeforeEach
  void setUp() 
  {
    mapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    mvc = standaloneSetup(controller)
      .build();
    initProducts();
  }
  
  private void initProducts()
  {
    user = new TestUserBuilder().builder()
      .withId(1L)
      .withEmail("kowalski@example.com")
      .build();
      
    UserDetails ud = new UserDetails();
    
    toAdd = new TestUserBuilder().builder()
      .withEmail("kowalski@example.com")
      .withPassword("tst")
      .withUserDetails(ud)
      .build();
      
    toAddInvalid = new TestUserBuilder().builder()
      .withEmail("kowalskiexample.com")
      .withUserDetails(null)
      .build();
  }

  // GET /api/user is Logged
  @Test
  public void testGetLoggedUserIfLogged() throws Exception
  {
    Mockito.when(service.getLoggedUser()).thenReturn(user);
    
    mvc.perform(get("/user"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(1)))
      .andExpect(jsonPath("$.email", Matchers.is("kowalski@example.com")));
      

    Mockito.verify(service, Mockito.times(1)).getLoggedUser();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  // GET /api/user isn't Logged
  @Test
  public void testGetLoggedUserIfNotLogged() throws Exception
  {
    Mockito.when(service.getLoggedUser())
      .thenThrow(new UserNotLoggedException(""));
    
    mvc.perform(get("/user"))
      .andExpect(status().isBadRequest());
      

    Mockito.verify(service, Mockito.times(1)).getLoggedUser();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  // PUT /api/user, NOT VALID
  @Test void testUpdateUserIfNotValid() throws Exception
  {
    mvc.perform(put("/user")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);
  }
  
  // PUT /api/user, VALID
  @Test void testUpdateUserIfValid() throws Exception
  {
    Mockito.when(service.updateUser(Mockito.any(User.class))).thenReturn(user);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(put("/user")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(1)))
      .andExpect(jsonPath("$.email", 
        Matchers.is("kowalski@example.com")));
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(service, Mockito.times(1)).updateUser(userCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getEmail(), Matchers.is("kowalski@example.com"));   
  }
  
  // POST /api/user, NOT VALID
  @Test void testRegisterUserIfNotValid() throws Exception
  {
    mvc.perform(post("/user")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);
  }
  
  // POST /api/user, VALID
  @Test void testRegisterUserIfValid() throws Exception
  {
    Mockito.when(service.registerUser(Mockito.any(User.class))).thenReturn(user);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/user")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(1)))
      .andExpect(jsonPath("$.email", 
        Matchers.is("kowalski@example.com")));
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(service, Mockito.times(1)).registerUser(userCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getEmail(), Matchers.is("kowalski@example.com"));   
  }
  
  // POST /api/user, EXISTS
  @Test void testRegisterUserIfExists() throws Exception
  {
    Mockito.when(service.registerUser(Mockito.any(User.class)))
      .thenThrow(new UserExistsException(""));
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/user")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isBadRequest());
    
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(service, Mockito.times(1)).registerUser(userCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getEmail(), Matchers.is("kowalski@example.com"));   
  }
  

}
