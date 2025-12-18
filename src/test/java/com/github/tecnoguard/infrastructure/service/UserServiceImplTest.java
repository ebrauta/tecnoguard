package com.github.tecnoguard.infrastructure.service;

import com.github.tecnoguard.core.exceptions.BusinessException;
import com.github.tecnoguard.core.exceptions.NotFoundException;
import com.github.tecnoguard.domain.enums.UserRole;
import com.github.tecnoguard.domain.models.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private PasswordEncoder encoder;

    private User user;
    private User user2;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");
        user.setUsername("user");
        user.setUserRole(UserRole.TECHNICIAN);
        user.setPassword("123");

        user2 = new User();
        user2.setName("User novo");
        user2.setEmail("novo@mail.com");
        user2.setUsername("usernovo");
        user2.setUserRole(UserRole.PLANNER);
        user2.setPassword("987");
    }

    @Test
    @DisplayName("UserService - Deve criar um usuário")
    void shouldCreateUser() {
        User u = service.create(user);

        Assertions.assertNotNull(u.getId());
        Assertions.assertEquals("User", u.getName());
        Assertions.assertEquals("user@mail.com", u.getEmail());
        Assertions.assertEquals("user", u.getUsername());
        Assertions.assertEquals("TECHNICIAN", u.getUserRole().toString());
        Assertions.assertTrue(encoder.matches("123", u.getPassword()));
        Assertions.assertTrue(u.isActive());
    }

    @Test
    @DisplayName("UserService - Deve atualizar um usuário somente se for admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateUserIfAdmin() {
        User u = service.create(user);
        User updated = service.update(u.getId(), user2);

        Assertions.assertEquals("User novo", updated.getName());
        Assertions.assertEquals("novo@mail.com", updated.getEmail());
        Assertions.assertEquals("PLANNER", updated.getUserRole().toString());
    }

    @Test
    @DisplayName("UserService - Deve atualizar um usuário somente se for o mesmo usuário")
    @WithMockUser(username = "user", roles = {"TECHNICIAN"})
    void shouldUpdateUserIfTheSameUser() {
        User u = service.create(user);
        User updated = service.update(u.getId(), user2);

        Assertions.assertEquals("User novo", updated.getName());
        Assertions.assertEquals("novo@mail.com", updated.getEmail());
        Assertions.assertEquals("PLANNER", updated.getUserRole().toString());
    }

    @Test
    @DisplayName("UserService - Deve lançar uma exception caso o usuário tente alterar outro usuário")
    @WithMockUser(username = "user_2", roles = {"OPERATOR"})
    void shouldThrowExceptionIfUpdateUser() {
        User u = service.create(user);

        Assertions.assertThrows(BusinessException.class, () -> service.update(u.getId(), user2));
    }

    @Test
    @DisplayName("UserService - Deve mudar a senha de um usuário somente se for admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldChangeUsersPasswordIfAdmin() {
        User u = service.create(user);
        service.changePassword(u.getId(), "123", "abc");

        Assertions.assertTrue(encoder.matches("abc", service.findById(u.getId()).getPassword()));
    }

    @Test
    @DisplayName("UserService - Deve mudar a senha de um usuário somente se for o mesmo usuário")
    @WithMockUser(username = "user", roles = {"Technician"})
    void shouldChangeUsersPasswordIfTheSameUser() {
        User u = service.create(user);
        service.changePassword(u.getId(), "123", "abc");

        Assertions.assertTrue(encoder.matches("abc", service.findById(u.getId()).getPassword()));
    }

    @Test
    @DisplayName("UserService - Deve lançar uma exception caso o usuário tente alterar senha de outro usuário")
    @WithMockUser(username = "user_2", roles = {"OPERATOR"})
    void shouldThrowExceptionIfChangePasswordAnotherUser() {
        User u = service.create(user);

        Assertions.assertThrows(BusinessException.class, () -> service.changePassword(u.getId(), "123", "abc"));
    }

    @Test
    @DisplayName("UserService - Deve desativar e reativar um usuário")
    void shouldDeactivateUser() {
        User u = service.create(user);
        service.deactivate(u.getId());
        Assertions.assertFalse(service.findById(u.getId()).isActive());

        service.reactivate(u.getId());
        Assertions.assertTrue(service.findById(u.getId()).isActive());
    }

    @Test
    @DisplayName("UserService - Deve listar usuários com paginação")
    void shouldListPaginatedUsers() {
        User a = service.create(user);
        User b = service.create(user2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());

        Page<User> result = service.list(pageable);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("UserService - Deve buscar usuário por ID")
    void shouldFindUserById() {
        User created = service.create(user);
        User found = service.findById(created.getId());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(created.getId(), found.getId());
        Assertions.assertEquals("user", found.getUsername());
    }

    @Test
    @DisplayName("UserService - Deve lançar exceção ao buscar ID inexistente")
    void shouldThrowWhenUserNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> service.findById(999L));
    }

    @Test
    @DisplayName("UserService - Deve respeitar limite de página")
    void shouldReturnLimitedPage() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            User u = new User();
            u.setName("User" + i);
            u.setEmail("user" + i + "@mail.com");
            u.setUsername("user" + i);
            u.setPassword("123");
            u.setUserRole(UserRole.TECHNICIAN);
            service.create(u);
        });

        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = service.list(pageable);

        Assertions.assertEquals(2, page.getContent().size());
        Assertions.assertEquals(3, page.getTotalPages());
    }
}