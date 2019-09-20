package com.example.groceryapi.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProducerHasProductsException extends RuntimeException
{
  public ProducerHasProductsException(String message) 
  {
    super(message);
  }
}
