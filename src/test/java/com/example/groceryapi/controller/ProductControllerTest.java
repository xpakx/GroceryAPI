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
import com.example.groceryapi.service.ProductService;
import com.example.groceryapi.error.NoSuchProductException;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.NoSuchCategoryException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;


public class ProductControllerTest 
{
  @Mock
  private ProductService service;

  @InjectMocks
  private ProductController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private Product first;
  private Product second;
  private Product toAdd;
  private Product toAddInvalid;

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
      
    first = new TestProductBuilder().builder()
      .withId(1L)
      .withName("pomarańcze")
      .withDescription("wyśmienite pomarańcze")
      .withPrice(new BigDecimal(10.00))
      .withOriginalPrice(new BigDecimal(10.00))
      .withProducer(producer)
      .withInStock(10)
      .withBought(2)
      .build();
    
    second = new TestProductBuilder().builder()
      .withId(2L)
      .withName("pomidory")
      .withDescription("wyśmienite pomidory")
      .withPrice(new BigDecimal(7.00))
      .withOriginalPrice(new BigDecimal(7.50))
      .withProducer(producer)
      .withInStock(25)
      .withBought(0)
      .build();    
    
    toAdd = new TestProductBuilder().builder()
      .withName("pomarańcze")
      .withDescription("wyśmienite pomarańcze")
      .withPrice(new BigDecimal(10.00))
      .withOriginalPrice(new BigDecimal(10.00))
      .withProducer(producer)
      .withInStock(10)
      .withBought(2)
      .build();
      
    String title = createStringWithLength(101);
    String description = createStringWithLength(3001);
      
    toAddInvalid = new TestProductBuilder().builder()
      .withName(title)
      .withDescription(description)
      .withPrice(new BigDecimal(10.00))
      .withOriginalPrice(new BigDecimal(10.00))
      .withProducer(producer)
      .withInStock(10)
      .withBought(2)
      .build();
  }

  // GET /api/product/all
  @Test
  public void testGetProductList() throws Exception
  {
    Mockito.when(service.findAll()).thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/product/all"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].name", Matchers.is("pomarańcze")))
      .andExpect(jsonPath("$[1].id", Matchers.is(2)))
      .andExpect(jsonPath("$[1].name", Matchers.is("pomidory")));
      

    Mockito.verify(service, Mockito.times(1)).findAll();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  // GET api/product/{id}, FOUND
  @Test
  public void testGetProductByIdIfFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenReturn(second);
 
    mvc.perform(get("/product/{id}", 2L))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(2)))
      .andExpect(jsonPath("$.name", Matchers.is("pomidory")));
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);    
  }
  
  @Test
  public void testGetProductByIdIfNotFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenThrow(new NoSuchProductException(""));
 
    mvc.perform(get("/product/{id}", 2L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);   
  }

  // POST /api/product 
  @Test
  public void testAddProduct() throws Exception 
  {         
    Mockito.when(service.addProduct(Mockito.any(Product.class))).thenReturn(first);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/product")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(1)))
      .andExpect(jsonPath("$.description", 
        Matchers.is("wyśmienite pomarańcze")))
      .andExpect(jsonPath("$.name", Matchers.is("pomarańcze")));
      
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    Mockito.verify(service, Mockito.times(1)).addProduct(productCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    Product productArgument = productCaptor.getValue();
    assertNull(productArgument.getId());
    assertThat(productArgument.getDescription(), 
      Matchers.is("wyśmienite pomarańcze"));
    assertThat(productArgument.getName(), Matchers.is("pomarańcze"));
  }
  
  // POST /api/product 
  @Test
  public void testAddProductIfNotValid() throws Exception 
  {   
    mvc.perform(post("/product")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);

  }
  
  // DELETE api/product/{id}
  @Test
  public void testDeleteProductByIdIfFound() throws Exception
  {   
    mvc.perform(delete("/product/{id}", 1L))
      .andExpect(status().is(204));
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).deleteProduct(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);  
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // DELETE api/product/{id}
  @Test
  public void testDeleteProductByIdIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchProductException(""))
      .when(service).deleteProduct(1L);

    
    mvc.perform(delete("/product/{id}", 1L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).deleteProduct(1L);
    Mockito.verifyNoMoreInteractions(service);      
  }

  // PUT api/product/{id}
  @Test void testUpdateProductByIdIfNotValid() throws Exception
  {
    mvc.perform(put("/product/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAddInvalid)))
      .andExpect(status().isBadRequest());
    
    Mockito.verifyZeroInteractions(service);    
  }  
 
  // PUT api/product/{id}
  @Test void testUpdateProductByIdIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchProductException(""))
      .when(service).updateProduct(Mockito.anyLong(), Mockito.any(Product.class));

    mvc.perform(put("/product/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isNotFound());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    Mockito.verify(service, Mockito.times(1)).updateProduct(idCaptor.capture(), 
      productCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Product productArgument = productCaptor.getValue();
    assertNull(productArgument.getId());
    assertThat(productArgument.getDescription(), 
      Matchers.is("wyśmienite pomarańcze"));
    assertThat(productArgument.getName(), Matchers.is("pomarańcze"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // PUT api/product/{id}
  @Test void testUpdateProductByIdIfFoundAndValid() throws Exception
  {
    Mockito.doNothing().when(service)
      .updateProduct(Mockito.anyLong(), Mockito.any(Product.class));
      
    mvc.perform(put("/product/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().is(204));
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    Mockito.verify(service, Mockito.times(1)).updateProduct(idCaptor.capture(), 
      productCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Product productArgument = productCaptor.getValue();
    assertNull(productArgument.getId());
    assertThat(productArgument.getDescription(), 
      Matchers.is("wyśmienite pomarańcze"));
    assertThat(productArgument.getName(), Matchers.is("pomarańcze"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L)); 
  }
  
  @Test
  public void testGetProductListByProducerIfProducerExists() throws Exception
  {
    Mockito.when(service.findAllByProducer("Kowalski & Sons"))
      .thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/product/byProducer/{name}", "Kowalski & Sons"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].name", Matchers.is("pomarańcze")))
      .andExpect(jsonPath("$[1].id", Matchers.is(2)))
      .andExpect(jsonPath("$[1].name", Matchers.is("pomidory")));
      

    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service, Mockito.times(1)).findAllByProducer(nameCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
    
    String nameArgument = nameCaptor.getValue();
    assertThat(nameArgument, Matchers.is("Kowalski & Sons"));
  }
  
  @Test
  public void testGetProductListByProducerIfProducerNotFound() throws Exception
  {
    Mockito.when(service.findAllByProducer("Nowak & Nowak"))
      .thenThrow(new NoSuchProducerException(""));
    
    mvc.perform(get("/product/byProducer/{name}", "Nowak & Nowak"))
      .andExpect(status().isNotFound());
      
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service, Mockito.times(1)).findAllByProducer(nameCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
    
    String nameArgument = nameCaptor.getValue();
    assertThat(nameArgument, Matchers.is("Nowak & Nowak"));
  }
  
  @Test
  public void testGetProductListByCategoryIfProducerExists() throws Exception
  {
    Mockito.when(service.findAllByCategory("owoce"))
      .thenReturn(Arrays.asList(first));
    
    mvc.perform(get("/product/byCategory/{name}", "owoce"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(1)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].name", Matchers.is("pomarańcze")));
      

    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service, Mockito.times(1)).findAllByCategory(nameCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
    
    String nameArgument = nameCaptor.getValue();
    assertThat(nameArgument, Matchers.is("owoce"));
  }
  
  @Test
  public void testGetProductListByCategoryIfProducerNotFound() throws Exception
  {
    Mockito.when(service.findAllByCategory("grzyby"))
      .thenThrow(new NoSuchCategoryException(""));
    
    mvc.perform(get("/product/byCategory/{name}", "grzyby"))
      .andExpect(status().isNotFound());
      
    ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service, Mockito.times(1)).findAllByCategory(nameCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
    
    String nameArgument = nameCaptor.getValue();
    assertThat(nameArgument, Matchers.is("grzyby"));
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
