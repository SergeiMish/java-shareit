package ru.practicum.shareit.user.model;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private String name;
}
