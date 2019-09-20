package com.example.groceryapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.groceryapi.entity.Producer;

@Repository
public interface ProducerRepository extends JpaRepository<Producer,Long> 
{
  Producer findByName(String name);
}


