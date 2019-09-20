package com.example.groceryapi.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserNotLoggedException extends RuntimeException
{
  public UserNotLoggedException(String message) 
  {
    super(message);
  }
}
