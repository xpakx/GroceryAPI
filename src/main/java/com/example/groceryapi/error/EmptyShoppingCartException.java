package com.example.groceryapi.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyShoppingCartException extends RuntimeException
{
  public EmptyShoppingCartException(String message) 
  {
    super(message);
  }
}
