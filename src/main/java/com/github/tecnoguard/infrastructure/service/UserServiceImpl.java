package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.domain.service.IUserService;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public User create(User user) {
        user.create();
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }
}
