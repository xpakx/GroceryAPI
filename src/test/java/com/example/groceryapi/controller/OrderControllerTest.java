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
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.OrderStatus;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.service.OrderService;
import com.example.groceryapi.error.NoSuchOrderException;
import com.example.groceryapi.error.EmptyShoppingCartException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class OrderControllerTest 
{
  @Mock
  private OrderService service;

  @InjectMocks
  private OrderController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private Order toAdd;
  private Order first;
  private Order second;

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
    
    ProductOrder firstProductOrder = new TestProductOrderBuilder().builder()
      .withId(1L)
      .withProduct(firstProduct)
      .withPrice(firstProduct.getPrice().multiply(new BigDecimal(3)))
      .withQuantity(new BigDecimal(3))
      .build();
      
    ProductOrder secondProductOrder = new TestProductOrderBuilder().builder()
      .withId(2L)
      .withProduct(secondProduct)
      .withPrice(firstProduct.getPrice().multiply(new BigDecimal(5)))
      .withQuantity(new BigDecimal(3))
      .build();
      
    ProductOrder thirdProductOrder = new TestProductOrderBuilder().builder()
      .withId(3L)
      .withProduct(firstProduct)
      .withPrice(firstProduct.getPrice().multiply(new BigDecimal(2)))
      .withQuantity(new BigDecimal(3))
      .build();
    
    List<ProductOrder> firstListOfProductOrders = new ArrayList<>();
    firstListOfProductOrders.add(firstProductOrder);
    
    List<ProductOrder> secondListOfProductOrders = new ArrayList<>();
    secondListOfProductOrders.add(secondProductOrder);   
    secondListOfProductOrders.add(thirdProductOrder);  
    
    first = new TestOrderBuilder().builder()
      .withId(1L)
      .withPrice(firstProductOrder.getPrice())
      .withDate(null) 
      .withOrderStatus(OrderStatus.SHIPPING)
      .withUser(null) 
      .withProductOrders(firstListOfProductOrders)
      .build();

    second = new TestOrderBuilder().builder()
      .withId(2L)
      .withPrice(secondProductOrder.getPrice().add(thirdProductOrder.getPrice()))
      .withDate(null) 
      .withOrderStatus(OrderStatus.NEW)
      .withUser(null) 
      .withProductOrders(secondListOfProductOrders)
      .build();
      
    toAdd = new TestOrderBuilder().builder()
      .withPrice(firstProductOrder.getPrice())
      .withDate(null) 
      .withOrderStatus(OrderStatus.SHIPPING)
      .withUser(null) 
      .withProductOrders(firstListOfProductOrders)
      .build();
  }

  // GET /api/order/all
  @Test
  public void testGetOrderList() throws Exception
  {
    Mockito.when(service.findAll()).thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/order/all"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].productOrders", Matchers.hasSize(1)))
      .andExpect(jsonPath("$[0].productOrders[0].product.name", Matchers.is("pomarańcze")))
      .andExpect(jsonPath("$[1].id", Matchers.is(2)))
      .andExpect(jsonPath("$[1].productOrders", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[1].productOrders[0].product.name", Matchers.is("mandarynki")))
      .andExpect(jsonPath("$[1].productOrders[1].product.name", Matchers.is("pomarańcze")));
      

    Mockito.verify(service, Mockito.times(1)).findAll();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  //GET /api/order/getStatuses
  @Test
  public void testGetStatuses() throws Exception
  {
    mvc.perform(get("/order/getStatuses"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(3)))
      .andExpect(jsonPath("$[0]", Matchers.is("Nowe")))
      .andExpect(jsonPath("$[1]", Matchers.is("Wysłane")))
      .andExpect(jsonPath("$[2]", Matchers.is("Anulowane")));
  }

  //GET /api/order/{id} NOT FOUND 
  @Test
  public void testGetOrderByIdIfNotFound() throws Exception
  {
    Mockito.when(service.findById(2L)).thenThrow(new NoSuchOrderException(""));
 
    mvc.perform(get("/order/{id}", 2L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);     
  }
  
  //GET /api/order/{id} FOUND
  @Test
  public void testGetOrderByIdIfFound() throws Exception
  {
    Mockito.when(service.findById(2L)).thenReturn(second);
 
    mvc.perform(get("/order/{id}", 2L))
      .andExpect(status().isOk())  //????
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(2)))
      .andExpect(jsonPath("$.productOrders", Matchers.hasSize(2)))
      .andExpect(jsonPath("$.productOrders[0].product.name", Matchers.is("mandarynki")))
      .andExpect(jsonPath("$.productOrders[1].product.name", Matchers.is("pomarańcze")));
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);      
  }
  
  //PUT /api/order/{id} NOT FOUND
  @Test void testUpdateOrderByIdIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchOrderException(""))
      .when(service).updateOrder(Mockito.anyLong(), Mockito.any(Order.class));

    mvc.perform(put("/order/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isNotFound());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    Mockito.verify(service, Mockito.times(1)).updateOrder(idCaptor.capture(), 
      orderCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Order orderArgument = orderCaptor.getValue();
    assertNull(orderArgument.getId());
    assertThat(orderArgument.getProductOrders().get(0).getProduct().getName(), 
      Matchers.is("pomarańcze")); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // PUT /api/category/{id}, FOUND
  @Test void testUpdateOrderByIdIfFound() throws Exception
  {
    Mockito.doNothing().when(service)
      .updateOrder(Mockito.anyLong(), Mockito.any(Order.class));
      
    mvc.perform(put("/order/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().is(204));
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    Mockito.verify(service, Mockito.times(1)).updateOrder(idCaptor.capture(), 
      orderCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Order orderArgument = orderCaptor.getValue();
    assertNull(orderArgument.getId());
    assertThat(orderArgument.getProductOrders().get(0).getProduct().getName(), 
      Matchers.is("pomarańcze")); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }  
  
  //POST /order
  @Test
  public void testSaveIfCartEmpty() throws Exception
  {
    Mockito.when(service.saveOrder()).thenThrow(new EmptyShoppingCartException(""));
    
    mvc.perform(post("/order")
        .contentType(APPLICATION_JSON_UTF8))
      .andExpect(status().isBadRequest()); 
      
    Mockito.verify(service, Mockito.times(1)).saveOrder();
    Mockito.verifyNoMoreInteractions(service);  
  }
  
  @Test
  public void testSave() throws Exception
  {
    Mockito.when(service.saveOrder()).thenReturn(second);
    
    mvc.perform(post("/order")
        .contentType(APPLICATION_JSON_UTF8))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.productOrders", Matchers.hasSize(2)))
      .andExpect(jsonPath("$.productOrders[0].product.name", Matchers.is("mandarynki")))
      .andExpect(jsonPath("$.productOrders[1].product.name", Matchers.is("pomarańcze"))); 
      
    Mockito.verify(service, Mockito.times(1)).saveOrder();
    Mockito.verifyNoMoreInteractions(service);     
  }
   
}
