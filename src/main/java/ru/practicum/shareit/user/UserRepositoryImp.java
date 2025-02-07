package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final Map<Long, User> userRepository = new HashMap<>();
    private long idCounter = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.remove(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.get(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.containsKey(id);
    }

    private long generateId() {
        return ++idCounter;
    }
}