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
import com.example.groceryapi.controller.ShoppingCartController;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.service.ShoppingCartService;
import com.example.groceryapi.error.NoSuchProductException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class ShoppingCartControllerTest 
{
  @Mock
  private ShoppingCartService service;

  @InjectMocks
  private ShoppingCartController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private ShoppingCart cart;
  private ProductOrder first;
  private ProductOrder second;
  private BigDecimal toAdd;

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
      
    Product firstProduct = new TestProductBuilder().builder()
      .withId(1L)
      .withName("pomarańcze")
      .withDescription("wyśmienite pomarańcze")
      .withPrice(new BigDecimal(10.00))
      .withOriginalPrice(new BigDecimal(10.00))
      .withProducer(producer)
      .withInStock(10)
      .withBought(2)
      .build();
    
    Product secondProduct = new TestProductBuilder().builder()
      .withId(2L)
      .withName("mandarynki")
      .withDescription("wyśmienite mandarynki")
      .withPrice(new BigDecimal(7.00))
      .withOriginalPrice(new BigDecimal(7.50))
      .withProducer(producer)
      .withInStock(25)
      .withBought(0)
      .build();    
    
    first = new TestProductOrderBuilder().builder()
      .withId(1L)
      .withProduct(firstProduct)
      .withPrice(firstProduct.getPrice().multiply(new BigDecimal(3)))
      .withQuantity(new BigDecimal(3))
      .build();
      
    second = new TestProductOrderBuilder().builder()
      .withId(2L)
      .withProduct(secondProduct)
      .withPrice(firstProduct.getPrice().multiply(new BigDecimal(5)))
      .withQuantity(new BigDecimal(3))
      .build();
      
    toAdd = new BigDecimal(10);
    
    cart = new ShoppingCart();
  }

  // GET /api/cart
  @Test
  public void testGetProductList() throws Exception
  {
    Mockito.when(service.getProductsInCart()).thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/cart"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8));
      

    Mockito.verify(service, Mockito.times(1)).getProductsInCart();
    Mockito.verifyNoMoreInteractions(service);
  }

  // DELETE /api/cart/{id}
  @Test
  public void testDeleteProductFromCart() throws Exception
  {
    Mockito.when(service.deleteFromCart(Mockito.anyLong())).thenReturn(cart);
    mvc.perform(delete("/cart/{id}", 1L))
      .andExpect(status().isOk()); 
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).deleteFromCart(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));      
  }
  
  // PUT /api/cart/{id}, NOT EXISTS
  @Test void testUpdateProductInCartIfNotExists() throws Exception
  {
    Mockito.when(service
      .updateProductInCart(Mockito.anyLong(), Mockito.any(BigDecimal.class)))
      .thenReturn(null);
    
    mvc.perform(put("/cart/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<BigDecimal> quantityCaptor = ArgumentCaptor.forClass(BigDecimal.class);
    Mockito.verify(service, Mockito.times(1)).updateProductInCart(idCaptor.capture(), 
      quantityCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    assertThat(quantityCaptor.getValue().intValue(), Matchers.is(10));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // PUT /api/cart/{id}, EXISTS
  @Test void testUpdateProductInCartIfExists() throws Exception
  {
    Mockito.when(service
      .updateProductInCart(Mockito.anyLong(), Mockito.any(BigDecimal.class)))
      .thenReturn(cart);
      
    mvc.perform(put("/cart/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<BigDecimal> quantityCaptor = ArgumentCaptor.forClass(BigDecimal.class);
    Mockito.verify(service, Mockito.times(1)).updateProductInCart(idCaptor.capture(), 
      quantityCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    assertThat(quantityCaptor.getValue().intValue(), Matchers.is(10));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // POST /api/cart
  @Test
  public void testAddToCart() throws Exception 
  {         
    Mockito.when(service.addToCart(Mockito.anyLong())).thenReturn(cart);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/cart")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(1L)))
      .andExpect(status().isOk());
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).addToCart(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
        
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));
  }
  
  // POST /api/cart
  @Test
  public void testAddToCartIfProductNotExists() throws Exception 
  {         
    Mockito.when(service.addToCart(Mockito.anyLong())).thenThrow(new NoSuchProductException(""));
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/cart")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(1L)))
      .andExpect(status().isNotFound());
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).addToCart(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
        
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));
  }
  

}
