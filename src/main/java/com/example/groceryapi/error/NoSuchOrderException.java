package com.example.groceryapi.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchOrderException extends RuntimeException
{
  public NoSuchOrderException(String message) 
  {
    super(message);
  }
}
