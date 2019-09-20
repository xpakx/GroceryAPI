package com.example.groceryapi.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.repository.ProducerRepository;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.ProducerHasProductsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.exception.ConstraintViolationException;
import java.util.Optional;

@Service
public class ProducerService
{
  private ProducerRepository producerRepository;
  
  @Autowired
  public ProducerService(ProducerRepository producerRepository)
  {
    this.producerRepository = producerRepository;
  }
  
  public List<Producer> findAll()
  {
    return producerRepository.findAll();
  }
  
  public Producer findById(long i) throws NoSuchProducerException
  {
    Optional<Producer> producer =  producerRepository.findById(i);
    if(!producer.isPresent()) 
    {
      throw new NoSuchProducerException("Nie istnieje producent o podanym ID");
    }
    return producer.get();
  }
  
  public void deleteProducer(long i) throws NoSuchProducerException, ProducerHasProductsException
  {
    Optional<Producer> producer =  producerRepository.findById(i);
    if(!producer.isPresent()) 
    {
      throw new NoSuchProducerException("Nie istnieje producent o podanym ID");
    }
    try
    {
      producerRepository.delete(producer.get());
    }
    catch(ConstraintViolationException ex)
    {
      throw new ProducerHasProductsException("W bazie istnieją produkty tego producenta i" +
        "nie może zostać usunięty");
    }
  }
  
  public void updateProducer(long i, Producer producer) throws NoSuchProducerException
  {
    Optional<Producer> oldProducer =  producerRepository.findById(i);
    if(!oldProducer.isPresent()) 
    {
      throw new NoSuchProducerException("Nie istnieje producent o podanym ID");
    }
    producer.setId(i);
    producerRepository.save(producer);
  }
  
  public Producer addProducer(Producer producer)
  {
    producerRepository.save(producer);
    return producer;
  }
}
