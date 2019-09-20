package com.example.groceryapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.junit.Assert.*;

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

import com.example.groceryapi.entity.Product;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Category;
import com.example.groceryapi.service.ProducerService;
import com.example.groceryapi.error.NoSuchProducerException;
import java.math.BigDecimal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class ProducerControllerTest 
{
  @Mock
  private ProducerService service;

  @InjectMocks
  private ProducerController controller;
  
  private MockMvc mvc;
  
  private ObjectMapper mapper;
  
  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType
  (
    MediaType.APPLICATION_JSON.getType(),
    MediaType.APPLICATION_JSON.getSubtype(),                        
    Charset.forName("utf8")
  );

  private Producer toAdd;
  private Producer first;
  private Producer second;

  @BeforeEach
  void setUp() 
  {
    mapper = new ObjectMapper();
    MockitoAnnotations.initMocks(this);
    mvc = standaloneSetup(controller)
      .build();
    initProducts();
  }
  
  private void initProducts()
  {
    first = new TestProducerBuilder().builder()
      .withId(1L)
      .withName("Kowalski & Sons")
      .build();
      
    second = new TestProducerBuilder().builder()
      .withId(2L)
      .withName("Nowak & Nowak")
      .build();
      
    toAdd = new TestProducerBuilder().builder()
      .withName("Nowak & Nowak")
      .build();
  }

  // GET /api/producer/all
  @Test
  public void testGetProducerList() throws Exception
  {
    Mockito.when(service.findAll()).thenReturn(Arrays.asList(first, second));
    
    mvc.perform(get("/producer/all"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", Matchers.hasSize(2)))
      .andExpect(jsonPath("$[0].id", Matchers.is(1)))
      .andExpect(jsonPath("$[0].name", Matchers.is("Kowalski & Sons")))
      .andExpect(jsonPath("$[1].id", Matchers.is(2)))
      .andExpect(jsonPath("$[1].name", Matchers.is("Nowak & Nowak")));
      

    Mockito.verify(service, Mockito.times(1)).findAll();
    Mockito.verifyNoMoreInteractions(service);
  }
  
  // GET api/producer/{id}, FOUND
  @Test
  public void testGetProducerByIdIfFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenReturn(second);
 
    mvc.perform(get("/producer/{id}", 2L))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(2)))
      .andExpect(jsonPath("$.name", Matchers.is("Nowak & Nowak")));
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);    
  }

  // GET api/producer/{id}, NOT FOUND  
  @Test
  public void testGetProducerByIdIfNotFound() throws Exception
  { 
    Mockito.when(service.findById(2L)).thenThrow(new NoSuchProducerException(""));
 
    mvc.perform(get("/producer/{id}", 2L))
      .andExpect(status().isNotFound());
 
    Mockito.verify(service, Mockito.times(1)).findById(2L);
    Mockito.verifyNoMoreInteractions(service);   
  }


  // DELETE /api/category/{id}, FOUND
  @Test
  public void testDeleteProducerIfFound() throws Exception
  {
    mvc.perform(delete("/producer/{id}", 1L))
      .andExpect(status().is(204));
      
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).deleteProducer(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));      
  }
  
  // DELETE /api/producer/{id}, NOT FOUND
  @Test
  public void testDeleteCategoryIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchProducerException(""))
      .when(service).deleteProducer(1L);
    
    mvc.perform(delete("/producer/{id}", 1L))
      .andExpect(status().isNotFound());
 
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    Mockito.verify(service, Mockito.times(1)).deleteProducer(idCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));        
  }
  
  // PUT /api/producer/{id}, NOT FOUND
  @Test void testUpdateProducerByIdIfNotFound() throws Exception
  {
    Mockito.doThrow(new NoSuchProducerException(""))
      .when(service).updateProducer(Mockito.anyLong(), Mockito.any(Producer.class));

    mvc.perform(put("/producer/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isNotFound());
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
    Mockito.verify(service, Mockito.times(1)).updateProducer(idCaptor.capture(), 
      producerCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Producer producerArgument = producerCaptor.getValue();
    assertNull(producerArgument.getId());
    assertThat(producerArgument.getName(), Matchers.is("Nowak & Nowak"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L));  
  }
  
  // PUT /api/category/{id}, FOUND 
  @Test void testUpdateCategoryByIdIfFound() throws Exception
  {
    Mockito.doNothing().when(service)
      .updateProducer(Mockito.anyLong(), Mockito.any(Producer.class));
      
    mvc.perform(put("/producer/{id}", 1L)
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().is(204));
     
    ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
    Mockito.verify(service, Mockito.times(1)).updateProducer(idCaptor.capture(), 
      producerCaptor.capture());
    Mockito.verifyNoMoreInteractions(service); 
    
    Producer producerArgument = producerCaptor.getValue();
    assertNull(producerArgument.getId());
    assertThat(producerArgument.getName(), Matchers.is("Nowak & Nowak"));
    
    Long idArgument = idCaptor.getValue();
    assertThat(idArgument, Matchers.is(1L)); 
  }
  
  // POST /api/producer
  @Test
  public void testAddProducer() throws Exception 
  {         
    Mockito.when(service.addProducer(Mockito.any(Producer.class))).thenReturn(second);
    
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    mvc.perform(post("/producer")
        .contentType(APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsBytes(toAdd)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$.id", Matchers.is(2)))
      .andExpect(jsonPath("$.name", Matchers.is("Nowak & Nowak")));
      
    ArgumentCaptor<Producer> producerCaptor = ArgumentCaptor.forClass(Producer.class);
    Mockito.verify(service, Mockito.times(1)).addProducer(producerCaptor.capture());
    Mockito.verifyNoMoreInteractions(service);
 
    Producer producerArgument = producerCaptor.getValue();
    assertNull(producerArgument.getId());
    assertThat(producerArgument.getName(), Matchers.is("Nowak & Nowak"));
  }
  

}
