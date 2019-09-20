package com.example.groceryapi.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Length;

@Entity
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Category
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotEmpty
  @Length(max = 100, message = "Nazwa kategorii nie moze być dłuższa niż 100 znaków")
  private String name;
  
  //@JsonIgnore
  @ManyToMany(mappedBy = "categories")
  private List<Product> products = new ArrayList<>();
}
