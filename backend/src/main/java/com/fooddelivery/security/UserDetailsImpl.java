package com.fooddelivery.security;

import com.fooddelivery.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {
    
    private final Long id;
    private final String username;
    private final String password;
    private final Integer role;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.getStatus() == 1;
        
        String roleName = switch (user.getRole()) {
            case 1 -> "ROLE_MERCHANT";
            case 2 -> "ROLE_RIDER";
            default -> "ROLE_USER";
        };
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
