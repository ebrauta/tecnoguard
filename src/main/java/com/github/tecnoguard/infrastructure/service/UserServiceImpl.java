package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.application.dtos.user.request.UpdateUserDTO;
import com.github.tecnoguard.application.mappers.users.UserMapper;
import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.core.exceptions.DuplicatedException;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.domain.models.User;
import com.github.tecnoguard.domain.service.IUserService;
import com.github.tecnoguard.core.service.ISystemLogService;
import com.github.tecnoguard.infrastructure.persistence.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final ISystemLogService logService;

    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder, ISystemLogService logService) {
        this.repo = repo;
        this.encoder = encoder;
        this.logService = logService;

    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "SYSTEM";
    }

    private Boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ADMIN"));
    }

    @Override
    @Transactional
    public User create(User user) {
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicatedException("Já existe um usuário com esse username.");
        }
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedException("Já existe um usuário com esse e-mail.");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        User response = repo.save(user);
        logService.log(
                "USER_CREATED",
                "USER",
                response.getId(),
                String.format("Usuário %s criado por %s", response.getUsername(), getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User found = findById(id);
        if(!found.getUsername().equals(getCurrentUser()) && !isAdmin()) {
            throw new BusinessException("Este usuário não pode alterar dados de outro usuário");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        User response = repo.save(user);
        logService.log(
                "USER_UPDATED",
                "USER",
                response.getId(),
                String.format("Usuário atualizado por %s", getCurrentUser())
        );
        return response;
    }

    @Override
    @Transactional
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User found = findById(id);
        if(!found.getUsername().equals(getCurrentUser()) && !isAdmin()){
            throw new BusinessException("Este usuário não pode mudar senha de outro usuário");
        }
        found.changePassword(encoder, currentPassword, newPassword);
        User response = repo.save(found);
        logService.log(
                "USER_PASSWORD_CHANGED",
                "USER",
                response.getId(),
                String.format("Senha alterada por %s", getCurrentUser())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        User found = findById(id);
        found.deactivate();
        User response = repo.save(found);
        logService.log(
                "USER_DEACTIVATED",
                "USER",
                response.getId(),
                String.format("Usuário desativado por %s", getCurrentUser())
        );
    }

    @Override
    @Transactional
    public void reactivate(Long id) {
        User found = findById(id);
        found.reactivate();
        User response = repo.save(found);
        logService.log(
                "USER_ACTIVATED",
                "USER",
                response.getId(),
                String.format("Usuário ativado por %s", getCurrentUser())
        );
    }
}
