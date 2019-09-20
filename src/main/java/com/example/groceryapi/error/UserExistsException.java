package com.example.groceryapi.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserExistsException extends RuntimeException
{
  public UserExistsException(String message) 
  {
    super(message);
  }
}
