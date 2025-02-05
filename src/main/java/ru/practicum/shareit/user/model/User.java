package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
