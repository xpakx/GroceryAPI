package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.groceryapi.validation.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.Valid;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotEmpty(groups = Register.class, message = "Hasło nie może być puste")
  private String password;
  
  @Email(groups = Register.class)
  @NotEmpty(groups = Register.class, message = "Email nie może być pusty")
  private String email;
  
  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Order> orders;
  
  @OneToOne(mappedBy = "user")
  @JsonIgnore
  private ShoppingCart shoppingCart; 
  
  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) 
  @Valid
  @NotNull(groups = ConfirmOrder.class) 
  private UserDetails userDetails;
  
  @ManyToMany
  private List<UserRole> roles = new ArrayList<>();

}
