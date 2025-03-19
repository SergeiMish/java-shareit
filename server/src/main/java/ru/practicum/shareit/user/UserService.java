package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User add(User user);

    User delete(User user);

    List<User> getAll();

    User get(Long id);

    User update(Long id, User updatedUser);

}
