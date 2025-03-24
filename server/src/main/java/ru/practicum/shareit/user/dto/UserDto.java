package ru.practicum.shareit.user.dto;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String email;
    private String name;
}