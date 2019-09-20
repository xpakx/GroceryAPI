package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.Length;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data 
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotNull(message = "Nazwa produktu nie moze być pusta")
  @Length(max = 100, message = "Nazwa produktu nie moze być dłuższa niż 100 znaków")
  private String name;
  
  @Column(length = 3000)
  @Length(max = 3000, message = "Opis produktu nie moze być dłuższy niż 3000 znaków")
  private String description;
  
  @NotNull(message = "Cena nie może być pusta")
  private BigDecimal price;
  
  private BigDecimal originalPrice; //przed ewentualnym rabatem
  
  @ManyToOne
  @JoinColumn(name = "producer_id")
  @NotNull(message = "Producent nie może być pusty")
  private Producer producer;
  
  @NotNull(message = "Liczba produktów nie może być pusta")
  private Integer inStock;
  
  private Integer bought;
  
  @URL(message = "URL")
  private String picture;
  
  @JsonIgnore
  @ManyToMany
  @JoinTable(name = "product_category", joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")})
  private List<Category> categories = new ArrayList<>();
  
}
