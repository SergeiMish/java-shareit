package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);

    User findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);
}
