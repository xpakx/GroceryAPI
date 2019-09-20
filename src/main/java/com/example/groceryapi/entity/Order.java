package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"order\"")
public class Order implements Comparable<Order>
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private BigDecimal price;
  
  private Timestamp date;
  
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus; 
  
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user; 
  
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id", referencedColumnName = "id")
  private List<ProductOrder> productOrders;
  
  @Override
  public int compareTo(Order o) 
  {
    return o.getId().compareTo(getId());
  }
  
}
