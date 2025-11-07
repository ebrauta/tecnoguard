package com.github.tecnoguard.domain.models;

import com.github.tecnoguard.core.exceptions.PasswordMismatchException;
import com.github.tecnoguard.core.exceptions.UserInactiveException;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.shared.models.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    private String username;
    private String password;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime lastLogin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
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
