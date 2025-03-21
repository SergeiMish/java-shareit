package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@Transactional
@Rollback
public class UserControllerTest {

    @Autowired
    private UserService userService;

    private UserDto userDtoRequest1;
    private UserDto userDtoRequest2;

    @BeforeEach
    public void setUp() {
        userDtoRequest1 = UserDto.builder()
                .email("user1@test.com")
                .name("user1")
                .build();

        userDtoRequest2 = UserDto.builder()
                .email("user2@test.com")
                .name("user2")
                .build();
    }

    @Test
    void create_shouldCreateUser() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userResponse1 = userService.add(userRequest1);
        UserDto responseDto = UserDtoMapper.toUserDto(userResponse1);

        Assertions.assertThat(responseDto.getEmail()).isEqualTo(userDtoRequest1.getEmail());
        Assertions.assertThat(responseDto.getName()).isEqualTo(userDtoRequest1.getName());
    }

    @Test
    void update_shouldUpdateUser() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userResponse1 = userService.add(userRequest1);

        User userRequest2 = UserDtoMapper.toUser(userDtoRequest2);
        User userResponse2 = userService.update(userResponse1.getId(), userRequest2);
        UserDto responseDto = UserDtoMapper.toUserDto(userResponse2);

        Assertions.assertThat(responseDto.getEmail()).isEqualTo(userDtoRequest2.getEmail());
        Assertions.assertThat(responseDto.getName()).isEqualTo(userDtoRequest2.getName());
    }

    @Test
    void update_shouldUpdateUserWithNonNullValues() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userResponse1 = userService.add(userRequest1);

        User updatedUser = User.builder()
                .name("newName")
                .email("newEmail@test.com")
                .build();

        User updatedResponse = userService.update(userResponse1.getId(), updatedUser);
        UserDto responseDto = UserDtoMapper.toUserDto(updatedResponse);

        Assertions.assertThat(responseDto.getName()).isEqualTo("newName");
        Assertions.assertThat(responseDto.getEmail()).isEqualTo("newEmail@test.com");
    }

    @Test
    void update_shouldNotUpdateIfUserNotFound() {
        User updateUser = User.builder()
                .email("newEmail@test.com")
                .name("test")
                .build();

        Assertions.assertThatThrownBy(() -> {
                    userService.update(1L, updateUser);
                }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void update_shouldUpdateOnlyNameIfEmailIsNull() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userResponse1 = userService.add(userRequest1);

        User updatedUser = User.builder()
                .name("newName")
                .build();

        User updatedResponse = userService.update(userResponse1.getId(), updatedUser);
        UserDto responseDto = UserDtoMapper.toUserDto(updatedResponse);

        Assertions.assertThat(responseDto.getName()).isEqualTo("newName");
        Assertions.assertThat(responseDto.getEmail()).isEqualTo(userDtoRequest1.getEmail());
    }

    @Test
    void update_shouldUpdateOnlyEmailIfNameIsNull() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userResponse1 = userService.add(userRequest1);

        User updatedUser = User.builder()
                .email("newEmail@test.com")
                .build();

        User updatedResponse = userService.update(userResponse1.getId(), updatedUser);
        UserDto responseDto = UserDtoMapper.toUserDto(updatedResponse);

        Assertions.assertThat(responseDto.getName()).isEqualTo(userDtoRequest1.getName());
        Assertions.assertThat(responseDto.getEmail()).isEqualTo("newEmail@test.com");
    }

    @Test
    void update_shouldThrowConflictExceptionIfEmailAlreadyTaken() {
        User userRequest1 = UserDtoMapper.toUser(userDtoRequest1);
        User userRequest2 = UserDtoMapper.toUser(userDtoRequest2);

        userService.add(userRequest1);
        userService.add(userRequest2);

        User updatedUser = User.builder()
                .email(userDtoRequest1.getEmail())
                .build();

        Assertions.assertThatThrownBy(() -> {
                    userService.update(userRequest2.getId(), updatedUser);
                }).isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email is already taken");
    }
}
