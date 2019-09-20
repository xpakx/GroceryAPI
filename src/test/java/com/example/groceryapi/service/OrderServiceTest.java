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


import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.OrderStatus;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.controller.ShoppingCartController;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.repository.CategoryRepository;
import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.repository.OrderRepository;
import com.example.groceryapi.repository.ShoppingCartRepository;
import com.example.groceryapi.service.ShoppingCartService;
import com.example.groceryapi.service.UserService;
import com.example.groceryapi.service.ProductService;
import com.example.groceryapi.error.NoSuchOrderException;
import java.math.BigDecimal;
import java.util.Optional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class OrderServiceTest
{
  @Mock
  private ProductRepository productRepository;
  @Mock
  private ProductService productService;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private UserService userService;
  @Mock
  private ShoppingCartRepository shoppingCartRepository;
  @Mock
  private ShoppingCartService shoppingCartService;

  @InjectMocks
  private OrderService service;
  


  private Order first;
  private Order second;
  private ShoppingCart cart;
  private User user;
  private Product product;

  @BeforeEach
  void setUp() 
  {
    MockitoAnnotations.initMocks(this);
    initProducts();
  }
  
  private void initProducts()
  {
    first = new Order();
    second = new Order();
    first.setId(1L);
    second.setId(2L);
    
    product = new Product();
    product.setId(1L);
    product.setName("pomidor");
    product.setInStock(10);
    product.setBought(5);
    
    ProductOrder productOrder = new ProductOrder();
    productOrder.setProduct(product);
    productOrder.setPrice(new BigDecimal(10.0));
    productOrder.setQuantity(new BigDecimal(2));
    
    cart =  new ShoppingCart();
    cart.setProductOrders(Arrays.asList(productOrder));
    
    user = new User();
    user.setShoppingCart(cart);
    cart.setUser(user);
  }

  @Test
  public void testFindAll() throws Exception
  {
    Mockito.when(orderRepository.findAll()).thenReturn(Arrays.asList(first, second));
    
    List<Order> result = service.findAll();
      
    assertNotNull(result);
    assertThat(result, IsCollectionWithSize.hasSize(2));
    assertThat(result, Matchers.hasItem(first));
    assertThat(result, Matchers.hasItem(second));
    Mockito.verify(orderRepository, Mockito.times(1)).findAll();
  }
  
  @Test
  public void testFindByIdIfNotFound() throws Exception
  {
    Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchOrderException.class, () -> service.findById(1L));
    Mockito.verify(orderRepository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testFindByIdifFound() throws Exception
  {
    Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    Order result = service.findById(1L);
    
    assertNotNull(result);
    assertThat(result, Matchers.is(first));
    Mockito.verify(orderRepository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testUpdateOrderIfNotFound() throws Exception
  {
    Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchOrderException.class, () -> service.updateOrder(1L, first));
    Mockito.verify(orderRepository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testUpdateOrderIfFound() throws Exception
  {
    Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    service.updateOrder(1L, first);
    
    Mockito.verify(orderRepository, Mockito.times(1)).findById(Mockito.anyLong());
    Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any(Order.class));
  }
  
  @Test
  public void testSaveOrder() throws Exception
  {
    Mockito.when(userService.getLoggedUser()).thenReturn(user);
    
    Order result = service.saveOrder();
    
    assertEquals(cart.getProductOrders(), result.getProductOrders());
    assertEquals(cart.getUser(), result.getUser());
    assertEquals(new Integer(8), product.getInStock());
    assertEquals(new Integer(7), product.getBought());
    assertThat(result.getOrderStatus(), Matchers.is(OrderStatus.NEW));
    
    Mockito.verify(userService, Mockito.times(1)).getLoggedUser();
    Mockito.verify(shoppingCartService, Mockito.times(1)).deleteShoppingCart(cart);
  }
}
