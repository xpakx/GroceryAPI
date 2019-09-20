package com.example.groceryapi.entity;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProductOrder implements Comparable<ProductOrder>
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToOne
  private Product product;

  private BigDecimal quantity;
  private BigDecimal price;  
  
  public void setProduct(Product product)
  {
    this.product = product;
    this.price = product.getPrice();
    this.quantity = BigDecimal.ONE;
  }
  

  public void updateQuantity(BigDecimal quantity)
  {
    this.quantity = quantity;
    this.price = product.getPrice().multiply(this.quantity);
  }
  
  @Override
  public int compareTo(ProductOrder o) 
  {
    return (new Long(product.getId())).compareTo(new Long(o.getProduct().getId()));
  }
}
