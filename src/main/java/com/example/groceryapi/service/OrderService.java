package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.Order;
import com.example.groceryapi.entity.OrderStatus;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.repository.ProducerRepository;
import com.example.groceryapi.repository.CategoryRepository;
import com.example.groceryapi.repository.OrderRepository;
import com.example.groceryapi.repository.UserRepository;
import com.example.groceryapi.repository.ShoppingCartRepository;
import com.example.groceryapi.error.NoSuchProductException;
import com.example.groceryapi.error.NoSuchOrderException;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.NoSuchCategoryException;
import com.example.groceryapi.error.EmptyShoppingCartException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;
import java.util.Optional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class OrderService
{
  private ProductRepository productRepository;
  private ProductService productService;
  private OrderRepository orderRepository;
  private UserService userService;
  private ShoppingCartRepository shoppingCartRepository;
  private ShoppingCartService shoppingCartService;
  
  @Autowired
  public OrderService(ProductRepository productRepository, ProductService productService,
    OrderRepository orderRepository, UserService userService,
    ShoppingCartRepository shoppingCartRepository, ShoppingCartService shoppingCartService)
  {
    this.productRepository = productRepository;
    this.productService = productService;
    this.orderRepository = orderRepository;
    this.userService = userService;
    this.shoppingCartRepository = shoppingCartRepository;
    this.shoppingCartService = shoppingCartService;
  }
  
  public List<Order> findAll()
  {
    return orderRepository.findAll().stream()
      .sorted()
      .collect(Collectors.toList());
  }
  
  public Order findById(long i)
  {
    Optional<Order> order =  orderRepository.findById(i);
    if(!order.isPresent()) 
    {
      throw new NoSuchOrderException("Nie istnieje zamówienie o podanym ID");
    }
    return order.get();
  }
  
  public void updateOrder(long i, Order order)
  {
    Optional<Order> oldOrder =  orderRepository.findById(i);
    if(!oldOrder.isPresent()) 
    {
      throw new NoSuchOrderException("Nie istnieje zamówienie o podanym ID");
    }
    order.setId(i);
    orderRepository.save(order);
  }
  
  public Order saveOrder()
  {
    User user = userService.getLoggedUser();
    ShoppingCart cart = user.getShoppingCart();
    
    if(cart.getProductOrders().isEmpty())
    {
      throw new EmptyShoppingCartException("Koszyk jest pusty");
    }
    
    BigDecimal totalPrice;
    totalPrice = cart.getProductOrders().stream()
       .map(ProductOrder::getPrice)
       .reduce(BigDecimal.ZERO, BigDecimal::add);
       
    Order order = new Order();
    order.setUser(user);
    order.setProductOrders(cart.getProductOrders());
    order.setDate(Timestamp.valueOf(LocalDateTime.now()));
    order.setOrderStatus(OrderStatus.NEW);
    order.setPrice(totalPrice);
    orderRepository.save(order);
    
    for(ProductOrder productOrder : order.getProductOrders())
    { 
      productOrder.getProduct().setInStock
        (productOrder.getProduct().getInStock() - productOrder.getQuantity().intValue());
      productOrder.getProduct().setBought
        (productOrder.getProduct().getBought() + productOrder.getQuantity().intValue());
      productService.updateProduct(productOrder.getProduct().getId(), productOrder.getProduct());
    }
    shoppingCartService.deleteShoppingCart(cart);
    
    //confirmationMailSender.send(order);
    return order;
  }
}
