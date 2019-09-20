package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.ShoppingCart;
import com.example.groceryapi.entity.ProductOrder;
import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.service.UserService;
import com.example.groceryapi.service.ProductService;
import com.example.groceryapi.repository.ShoppingCartRepository;
import com.example.groceryapi.error.NoSuchProductException;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService
{  
  private UserService userService;
  private ProductService productService;
  private ShoppingCartRepository shoppingCartRepository;
  
  @Autowired
  public ShoppingCartService(UserService userService, ProductService productService,
  ShoppingCartRepository shoppingCartRepository)
  {
    this.userService = userService;
    this.productService = productService;
    this.shoppingCartRepository = shoppingCartRepository;
  }
  
  public ShoppingCart addToCart(long id) throws NoSuchProductException
  {
    ShoppingCart cart = getShoppingCart();
    
    Product product = productService.findById(id);
    if(product == null)
    {
      throw new NoSuchProductException("Nie istnieje produkt o podanym ID");
    }
    
    ProductOrder productOrder = new ProductOrder();
    productOrder.setProduct(product);
    
    Optional<ProductOrder> productInCart = cart.getProductOrders().stream()
      .filter(prod -> prod.getProduct().getId().equals(product.getId()))
      .findAny();
    if(productInCart.isPresent())
    {
      productOrder = productInCart.get();
      productOrder.updateQuantity(productOrder.getQuantity().add(BigDecimal.ONE));
      cart.getProductOrders().remove(productInCart);
    }

    cart.getProductOrders().add(productOrder);
    saveShoppingCart(cart);

    return cart;
  }
  
  public ShoppingCart deleteFromCart(long id)
  {
    ShoppingCart cart = getShoppingCart();
    
    Optional<ProductOrder> productInCart = cart.getProductOrders().stream()
      .filter(prod -> prod.getProduct().getId().equals(id))
      .findAny();
      
    if(productInCart.isPresent())
    {
      cart.getProductOrders().remove(productInCart.get());
    }
    
    saveShoppingCart(cart);
    return cart;
  }
  
  public List<ProductOrder> getProductsInCart()
  {
    ShoppingCart cart = getShoppingCart();
    if(cart.getProductOrders() == null)
    {
      return null;
    }
    return cart.getProductOrders().stream()
      .sorted()
      .collect(Collectors.toList());
  }
  
  public ShoppingCart updateProductInCart(long id, BigDecimal quantity)
  {
    ShoppingCart cart = getShoppingCart();
    
    Optional<ProductOrder> productInCart = cart.getProductOrders().stream()
      .filter(prod -> prod.getProduct().getId().equals(id))
      .findAny();
    
    if (productInCart.isPresent()) 
    {
      cart.getProductOrders().remove(productInCart);
      productInCart.get().updateQuantity(quantity);
      if(productInCart.get().getQuantity().intValue() > 0)
        cart.getProductOrders().add(productInCart.get());
    }
  
    saveShoppingCart(cart);
    return cart;
  }
  
  public void deleteShoppingCart(ShoppingCart cart)
  {
    shoppingCartRepository.delete(cart);
  }
  
  private ShoppingCart getShoppingCart() 
  {
    ShoppingCart cart = new ShoppingCart();
    if(userService.isUserLogged()) 
    {
      User user = userService.getLoggedUser();
      if(user.getShoppingCart() == null) 
      {
        cart.setUser(user);
      } 
      else 
      {
        cart = user.getShoppingCart();
      }
    } 
    return cart;
  }
  
  private void saveShoppingCart(ShoppingCart cart)
  {
    if(userService.isUserLogged()) 
    {
      shoppingCartRepository.save(cart);
    }
  }
}
