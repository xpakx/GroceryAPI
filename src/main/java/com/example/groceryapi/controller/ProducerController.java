package com.example.groceryapi.controller;

import com.example.groceryapi.repository.ProductRepository;
import com.example.groceryapi.entity.Producer;
import com.example.groceryapi.entity.Product;
import com.example.groceryapi.service.ProducerService;
import com.example.groceryapi.error.NoSuchProducerException;
import com.example.groceryapi.error.ProducerHasProductsException;
//import com.example.groceryapi.error.NoSuchProductException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/producer")
public class ProducerController
{
  private ProducerService producerService;
  
  @Autowired
  public void setService(ProducerService productService)
  {
    this.producerService = producerService;
  }
  
  //lista producentów
  @GetMapping("/all")
  public List<Producer> findAll()
  {
    return producerService.findAll();
  }
  
  @GetMapping("/{id}")
  public Producer findById(@PathVariable Long id) throws NoSuchProducerException
  {
    return producerService.findById(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) throws NoSuchProducerException, ProducerHasProductsException //+odmowa dostępu
  {
    producerService.deleteProducer(id);
  }
  
  @PutMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void updateById(@PathVariable Long id, @Valid @RequestBody Producer producer) throws NoSuchProducerException //+odmowaDostępu +walidacja
  {
    producerService.updateProducer(id, producer);
  }
  
  @PostMapping
  public Producer add(@Valid @RequestBody Producer producer)
  {
    return producerService.addProducer(producer);
  }  
}
