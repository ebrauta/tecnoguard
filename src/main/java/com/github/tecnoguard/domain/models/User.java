package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.PasswordMismatchException;
import com.github.tecnoguard.core.exceptions.UserInactiveException;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.core.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
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

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void changePassword(PasswordEncoder encoder, String curPass, String newPass) {
        validatePassword(encoder, curPass);
        this.password = encoder.encode(newPass);
    }

    private void validatePassword(PasswordEncoder encoder, String currentPassword){
        if (!encoder.matches(currentPassword, this.password)) {
            throw new PasswordMismatchException("Senha anterior inválida");
        }
    }

    public void deactivate() {
        if (!this.active) throw new UserInactiveException("Usuário já inativo");
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }
}
