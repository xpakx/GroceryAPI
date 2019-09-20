package com.example.groceryapi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class UserRole
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
    
  private String role;
  
  @ManyToMany(mappedBy = "roles")
  @JsonIgnore
  private List<User> users = new ArrayList<>();
    
}
