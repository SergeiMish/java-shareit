package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        if (isEmailTaken(user.getEmail())) {
            throw new ConflictException("Email is already taken");
        }
        return userRepository.save(user);
    }

    @Override
    public User delete(User user) {
        userRepository.deleteById(user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public User update(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (isEmailTaken(updatedUser.getEmail())) {
                throw new ConflictException("Email is already taken");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        return userRepository.save(existingUser);
    }

    private boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}