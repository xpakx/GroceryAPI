package com.example.groceryapi.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.Assert.*;
import org.hamcrest.collection.IsCollectionWithSize;

import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import org.springframework.http.MediaType;
import java.nio.charset.Charset;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertThrows;


import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.controller.ProducerController;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.repository.ProducerRepository;
import com.example.groceryapi.error.NoSuchProducerException;
import java.math.BigDecimal;
import java.util.Optional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class ProducerServiceTest
{
  @Mock
  private ProducerRepository repository;

  @InjectMocks
  private ProducerService service;

  private Producer first;
  private Producer second;

  @BeforeEach
  void setUp() 
  {
    MockitoAnnotations.initMocks(this);
    initProducts();
  }
  
  private void initProducts()
  {
    first = new Producer();
    second = new Producer();
  }

  @Test
  public void testFindAll() throws Exception
  {
    Mockito.when(repository.findAll()).thenReturn(Arrays.asList(first, second));
    
    List<Producer> result = service.findAll();
      
    assertNotNull(result);
    assertThat(result, IsCollectionWithSize.hasSize(2));
    assertThat(result, Matchers.hasItem(first));
    assertThat(result, Matchers.hasItem(second));
    Mockito.verify(repository, Mockito.times(1)).findAll();
  }
  
  @Test
  public void testFindByIdIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProducerException.class, () -> service.findById(1L));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testFindByIdifFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    Producer result = service.findById(1L);
    
    assertNotNull(result);
    assertThat(result, Matchers.is(first));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testDeleteProducerIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProducerException.class, () -> service.deleteProducer(1L));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testDeleteProducerIfFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    service.deleteProducer(1L);
    
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
    Mockito.verify(repository, Mockito.times(1)).delete(Mockito.any(Producer.class));
  }
  
  @Test
  public void testUpdateProducerIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProducerException.class, () -> service.updateProducer(1L, first));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testUpdateProducerIfFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    service.updateProducer(1L, first);
    
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Producer.class));
  }
  
  @Test
  public void testAddProducer() throws Exception
  {
    Producer result = service.addProducer(first);
    
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Producer.class));
    assertThat(result, Matchers.is(first));
  }


}
