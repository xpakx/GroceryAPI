package com.example.groceryapi.security;

import com.example.groceryapi.entity.User;
import com.example.groceryapi.entity.UserRole;
import com.example.groceryapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService 
{
  private UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository) 
  {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
  {
    User user = userRepository.findByEmail(username);
    if(user == null)
    {
      throw new UsernameNotFoundException("Użytkownik nie istnieje");
    }
    
    return new org.springframework.security.core.userdetails.User
    (
      user.getEmail(),
      user.getPassword(),
      convertAuthorities(user)
    );

  }

  private List<GrantedAuthority> convertAuthorities(User user)
  {
    List<GrantedAuthority> authorities = new ArrayList<>();
    
    for(UserRole role: user.getRoles())
    {
      authorities.add(new SimpleGrantedAuthority(role.getRole()));
    }
    
    //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    //if(user.getEmail().equals("admin@example.com")) {authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));}
    
    return authorities;
  }
}
