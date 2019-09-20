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
import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.repository.CategoryRepository;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.NoSuchProductException;
import com.example.groceryapi.error.NoSuchCategoryException;
import java.math.BigDecimal;
import java.util.Optional;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class ProductServiceTest
{
  @Mock
  private ProducerRepository producerRepository;
  @Mock
  private ProductRepository repository;
  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private ProductService service;

  private Product first;
  private Product second;
  private Producer producer;
  private Category category;

  @BeforeEach
  void setUp() 
  {
    MockitoAnnotations.initMocks(this);
    initProducts();
  }
  
  private void initProducts()
  {
    producer = new Producer();
    producer.setName("Kowalski");
    
    first = new Product();
    first.setName("pieczarki");
    first.setProducer(producer);
    producer.setProducts(Arrays.asList(first));
    
    second = new Product();
    
    category = new Category();
    category.setProducts(Arrays.asList(first));
  }

  @Test
  public void testFindAll() throws Exception
  {
    Mockito.when(repository.findAll()).thenReturn(Arrays.asList(first, second));
    
    List<Product> result = service.findAll();
      
    assertNotNull(result);
    assertThat(result, IsCollectionWithSize.hasSize(2));
    assertThat(result, Matchers.hasItem(first));
    assertThat(result, Matchers.hasItem(second));
    Mockito.verify(repository, Mockito.times(1)).findAll();
  }
  
  @Test
  public void testFindAllByProducerIfNotFound() throws Exception
  {
    Mockito.when(producerRepository.findByName(Mockito.anyString())).thenReturn(null);
    
    List<Product> result = service.findAll();
    assertThrows(NoSuchProducerException.class, () -> service.findAllByProducer("Kowalski"));
    Mockito.verify(producerRepository, Mockito.times(1)).findByName("Kowalski");
  }
  
  @Test
  public void testFindAllByProducerIfFound() throws Exception
  {
    Mockito.when(producerRepository.findByName(Mockito.anyString()))
      .thenReturn(producer);
    
    List<Product> result = service.findAllByProducer("Kowalski");
      
    assertNotNull(result);
    assertThat(result, IsCollectionWithSize.hasSize(1));
    assertThat(result, Matchers.hasItem(first));
    Mockito.verify(producerRepository, Mockito.times(1)).findByName("Kowalski");
  }
  
  ///
  @Test
  public void testFindAllByCategoryIfNotFound() throws Exception
  {
    Mockito.when(categoryRepository.findByName(Mockito.anyString())).thenReturn(null);
    
    List<Product> result = service.findAll();
    assertThrows(NoSuchCategoryException.class, () -> service.findAllByCategory("grzyby"));
    Mockito.verify(categoryRepository, Mockito.times(1)).findByName("grzyby");
  }
  
  @Test
  public void testFindAllByCategoryIfFound() throws Exception
  {
    Mockito.when(categoryRepository.findByName(Mockito.anyString()))
      .thenReturn(category);
    
    List<Product> result = service.findAllByCategory("grzyby");
      
    assertNotNull(result);
    assertThat(result, IsCollectionWithSize.hasSize(1));
    assertThat(result, Matchers.hasItem(first));
    Mockito.verify(categoryRepository, Mockito.times(1)).findByName("grzyby");
  }
  
  @Test
  public void testFindByIdIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProductException.class, () -> service.findById(1L));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testFindByIdifFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    Product result = service.findById(1L);
    
    assertNotNull(result);
    assertThat(result, Matchers.is(first));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testDeleteProductIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProductException.class, () -> service.deleteProduct(1L));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testDeleteProductIfFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    service.deleteProduct(1L);
    
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
    Mockito.verify(repository, Mockito.times(1)).delete(Mockito.any(Product.class));
  }
  
  @Test
  public void testUpdateProductIfNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    
    assertThrows(NoSuchProductException.class, () -> service.updateProduct(1L, first));
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
  }
  
  @Test
  public void testUpdateProductIfFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    
    service.updateProduct(1L, first);
    
    Mockito.verify(repository, Mockito.times(1)).findById(Mockito.anyLong());
    Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Product.class));
  }
  
  @Test
  public void testAddProduct() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    Mockito.when(producerRepository.findByName("Kowalski")).thenReturn(producer);
    
    Product result = service.addProduct(first);
    
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    
    Mockito.verify(repository, Mockito.times(1)).save(productCaptor.capture());
    assertThat(result, Matchers.is(first));
    assertThat(result.getProducer().getName(), Matchers.is("Kowalski"));
    assertThat(result.getName(), Matchers.is("pieczarki"));
    
    Product productArgument = productCaptor.getValue();
    assertThat(productArgument.getName(), Matchers.is("pieczarki"));  
  }
  
  @Test
  public void testAddProductIfProducerNotFound() throws Exception
  {
    Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(first));
    Mockito.when(producerRepository.findByName("Kowalski")).thenReturn(null);
    
    Product result = service.addProduct(first);
    
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
    
    Mockito.verify(repository, Mockito.times(1)).save(productCaptor.capture());
    Mockito.verify(producerRepository, Mockito.times(1)).save(producerCaptor.capture());
    assertThat(result, Matchers.is(first));
    assertThat(result.getProducer().getName(), Matchers.is("Kowalski"));
    assertThat(result.getName(), Matchers.is("pieczarki"));
    
    Product productArgument = productCaptor.getValue();
    assertThat(productArgument.getName(), Matchers.is("pieczarki"));      
    Producer producerArgument = producerCaptor.getValue();
    assertThat(producerArgument.getName(), Matchers.is("Kowalski"));  
  }


}
