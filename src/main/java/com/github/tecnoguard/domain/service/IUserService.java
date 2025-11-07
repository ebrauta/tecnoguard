package com.github.tecnoguard.domain.service;

import com.github.tecnoguard.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    User create(User user);
    User update(Long id, User user);
    void changePassword(Long id, String currentPassword, String newPassword);
    Page<User> list(Pageable pageable);
    User findById(Long id);
    void deactivate(Long id);
    void reactivate(Long id);
}
