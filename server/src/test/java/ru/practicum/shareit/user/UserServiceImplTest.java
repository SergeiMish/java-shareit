package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1L, "test@email.com", "Test User");
    }

    @Test
    void testAddUser_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.add(testUser);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
    }

    @Test
    void testAddUser_EmailTaken() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(ConflictException.class, () -> userService.add(testUser));
        verify(userRepository, never()).save(testUser);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = userService.delete(testUser);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser, new User(2L, "test2@email.com", "Test User 2"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = userService.get(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.get(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void testUpdateUser_Success() {
        User updatedUser = new User(testUser.getId(), "new@email.com", "New Name");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.update(testUser.getId(), updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), (result.getEmail()));
        assertEquals(updatedUser.getName(), result.getName());
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).save(updatedUser);
    }

    @Test
    void testUpdateUser_EmailTaken() {
        User updatedUser = new User(testUser.getId(), "existing@email.com", "New Name");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(ConflictException.class, () -> userService.update(testUser.getId(), updatedUser));
        verify(userRepository).findById(testUser.getId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        User updatedUser = new User(999L, "new@email.com", "New Name");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.update(999L, updatedUser));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testIsEmailTaken_True() {
        try {
            Method method = UserServiceImpl.class
                    .getDeclaredMethod("isEmailTaken", String.class);
            method.setAccessible(true);

            when(userRepository.findByEmail(testUser.getEmail()))
                    .thenReturn(Optional.of(testUser));

            boolean result = (boolean) method.invoke(userService, testUser.getEmail());

            assertTrue(result);
            verify(userRepository).findByEmail(testUser.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsEmailTaken_False() {
        try {
            Method method = UserServiceImpl.class
                    .getDeclaredMethod("isEmailTaken", String.class);
            method.setAccessible(true);

            when(userRepository.findByEmail("new@email.com"))
                    .thenReturn(Optional.empty());

            boolean result = (boolean) method.invoke(userService, "new@email.com");

            assertFalse(result);
            verify(userRepository).findByEmail("new@email.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}