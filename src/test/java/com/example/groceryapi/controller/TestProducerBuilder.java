package com.example.groceryapi.controller;

import lombok.Builder;
import com.example.groceryapi.entity.Producer;

public class TestProducerBuilder 
{
    @Builder(builderMethodName = "builder")
    public static Producer newProducer(Long withId, String withName) 
    {
      Producer producer = new Producer();
      producer.setId(withId);
      producer.setName(withName);
      return producer;
    }
}
