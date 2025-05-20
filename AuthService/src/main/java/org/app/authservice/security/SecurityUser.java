package org.app.authservice.security;
import org.app.authservice.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;

public class SecurityUser implements UserDetails {

  private final String id;
  private final String username;
  private final String password;
  private final String role;


  public SecurityUser(String id, String username, String password, String  role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;

  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
  }

  public String getId() {
    return id;
  }

  @Override
  public String getPassword() {
    return password;
  }


  @Override
  public String getUsername() {
    return username;
  }

//  @Override
//  public boolean isAccountNonExpired() {
//    return active;
//  }
//
//  @Override
//  public boolean isAccountNonLocked() {
//    return active;
//  }
//
//  @Override
//  public boolean isCredentialsNonExpired() {
//    return active;
//  }
//
//  @Override
//  public boolean isEnabled() {
//    return active;
//  }
}
