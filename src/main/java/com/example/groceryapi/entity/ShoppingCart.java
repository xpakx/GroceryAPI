package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ShoppingCart
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToOne
  private User user; 
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "shoppingcart_id", referencedColumnName = "id")
  private List<ProductOrder> productOrders;
  
}
