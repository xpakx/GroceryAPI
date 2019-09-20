package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> 
{
  UserRole findByRole(String role);
}


