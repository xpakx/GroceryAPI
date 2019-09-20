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

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.service.CategoryService;
import com.example.groceryapi.error.NoSuchCategoryException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class CategoryControllerTest 
{
  @Mock
  private CategoryService service;

  @InjectMocks
  private CategoryController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private Product firstProduct;
  private Product secondProduct;
  private Category toAdd;
  private Category toAddInvalid;
  private Category first;
  private Category second;
  private Category third;

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
    Producer producer = new TestProducerBuilder().builder()
      .withId(1L)
      .withName("Kowalski & Sons")
      .build();
      
    firstProduct = new TestProductBuilder().builder()
      .withId(1L)
      .withName("pomarańcze")
      .withDescription("wyśmienite pomarańcze")
      .withPrice(new BigDecimal(10.00))
      .withOriginalPrice(new BigDecimal(10.00))
      .withProducer(producer)
      .withInStock(10)
      .withBought(2)
      .build();
    
    secondProduct = new TestProductBuilder().builder()
      .withId(2L)
      .withName("mandarynki")
      .withDescription("wyśmienite mandarynki")
      .withPrice(new BigDecimal(7.00))
      .withOriginalPrice(new BigDecimal(7.50))
      .withProducer(producer)
      .withInStock(25)
      .withBought(0)
      .build();    
    
    List<Product> listOfProducts = new ArrayList<>();
    listOfProducts.add(firstProduct);
    listOfProducts.add(secondProduct);
    
    first = new TestCategoryBuilder().builder()
      .withId(1L)
      .withName("owoce")
      .withProducts(listOfProducts)
      .build();
      
    second = new TestCategoryBuilder().builder()
      .withId(2L)
      .withName("warzywa")
      .build();
      
    third = new TestCategoryBuilder().builder()
      .withId(3L)
      .withName("grzyby")
      .build();
      
    toAdd = new TestCategoryBuilder().builder()
      .withName("grzyby")
      .build();
      
    String name = createStringWithLength(101);
      
    toAddInvalid = new TestCategoryBuilder().builder()
      .withName(name)
      .build();
  }

  // GET /api/category/all
  @Test
  public void testGetCategoryList() throws Exception
  {
    Mockito.when(service.findAll()).thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/category/all"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].name", Matchers.is("owoce")))
      .andExpect(jsonPath("$[1].id", Matchers.is(2)))
      .andExpect(jsonPath("$[1].name", Matchers.is("warzywa")));
      

    Mockito.verify(service, Mockito.times(1)).findAll();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  // GET api/category/{id}, FOUND
  @Test
  public void testGetCategoryByIdIfFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenReturn(second);
 
    mvc.perform(get("/category/{id}", 2L))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(2)))
      .andExpect(jsonPath("$.name", Matchers.is("warzywa")));
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);    
  }

  // GET api/category/{id}, NOT FOUND  
  @Test
  public void testGetCategoryByIdIfNotFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenThrow(new NoSuchCategoryException(""));
 
    mvc.perform(get("/category/{id}", 2L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);   
  }


  // DELETE /api/category/{id}, FOUND
  @Test
  public void testDeleteCategoryIfFound() throws Exception
  {
    mvc.perform(delete("/category/{id}", 1L))
      .andExpect(status().is(204));
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).deleteCategory(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));      
  }
  
  // DELETE /api/category/{id}, NOT FOUND
  @Test
  public void testDeleteCategoryIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchCategoryException(""))
      .when(service).deleteCategory(1L);
    
    mvc.perform(delete("/category/{id}", 1L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).deleteCategory(1L);
    Mockito.verifyNoMoreInteractions(service);         
  }
  
  // PUT /api/category/{id}, NOT VALID
  @Test void testUpdateCategoryByIdIfNotValid() throws Exception
  {
    mvc.perform(put("/category/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);    
  }  
 
  // PUT /api/category/{id}, NOT FOUND
  @Test void testUpdateCategoryByIdIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchCategoryException(""))
      .when(service).updateCategory(Mockito.anyLong(), Mockito.any(Category.class));

    mvc.perform(put("/category/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isNotFound());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
    Mockito.verify(service, Mockito.times(1)).updateCategory(idCaptor.capture(), 
      categoryCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Category categoryArgument = categoryCaptor.getValue();
    assertNull(categoryArgument.getId());
    assertThat(categoryArgument.getName(), Matchers.is("grzyby"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // PUT /api/category/{id}, FOUND AND VALID
  @Test void testUpdateCategoryByIdIfFoundAndValid() throws Exception
  {
    Mockito.doNothing().when(service)
      .updateCategory(Mockito.anyLong(), Mockito.any(Category.class));
      
    mvc.perform(put("/category/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().is(204));
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
    Mockito.verify(service, Mockito.times(1)).updateCategory(idCaptor.capture(), 
      categoryCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Category categoryArgument = categoryCaptor.getValue();
    assertNull(categoryArgument.getId());
    assertThat(categoryArgument.getName(), Matchers.is("grzyby"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L)); 
  }
  
  // POST /api/category, VALID
  @Test
  public void testAddCategory() throws Exception 
  {         
    Mockito.when(service.addCategory(Mockito.any(Category.class))).thenReturn(third);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/category")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(3)))
      .andExpect(jsonPath("$.name", Matchers.is("grzyby")));
      
    ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
    Mockito.verify(service, Mockito.times(1)).addCategory(categoryCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    Category categoryArgument = categoryCaptor.getValue();
    assertNull(categoryArgument.getId());
    assertThat(categoryArgument.getName(), Matchers.is("grzyby"));
  }
  
  // POST /api/category, NOT VALID 
  @Test
  public void testAddCategoryIfNotValid() throws Exception 
  {   
    mvc.perform(post("/category")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);

  }

  public static String createStringWithLength(int length) 
  {
    StringBuilder builder = new StringBuilder();
 
    for (int index = 0; index < length; index++) 
    {
      builder.append("a");
    }
 
    return builder.toString();
  }
   
}
