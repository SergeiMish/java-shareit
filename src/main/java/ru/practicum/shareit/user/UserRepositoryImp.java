package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final HashMap<Long, User> userRepository = new HashMap<>();
    private long idCounter = 0;

    @Override
    public synchronized List<User> findAll() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public synchronized User save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public synchronized void deleteById(Long id) {
        userRepository.remove(id);
    }

    @Override
    public synchronized User findById(Long id) {
        return userRepository.get(id);
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) {
        return userRepository.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    private synchronized long generateId() {
        return ++idCounter;
    }
}