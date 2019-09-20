package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.UserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails,Long> 
{

}


