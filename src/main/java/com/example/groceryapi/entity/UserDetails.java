package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotEmpty;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class UserDetails
{
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotEmpty
  private String firstName;
  
  @NotEmpty
  private String lastName;
  
  @NotEmpty
  private String city;
  
  @NotEmpty
  private String postalCode;
  
  @NotEmpty
  private String houseNumber;
  
  @NotEmpty
  private String street;
  
  @NotEmpty
  private String phoneNumber;
  
}
