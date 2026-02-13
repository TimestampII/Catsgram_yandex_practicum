package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;


import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateEmail(user.getEmail());
        checkEmailDuplicate(user.getEmail(), null);

        user.setId(getNextUserId());
        user.setRegistrationDate(Instant.now());

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {

        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found");
        }
        validateEmail(user.getEmail());
        checkEmailDuplicate(user.getEmail(), user.getId());

        User oldUser = users.get(user.getId());
        oldUser.setEmail(user.getEmail());
        return oldUser;


    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
    }

    private void checkEmailDuplicate(String email, Long currentUserId) {
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                if (currentUserId == null || !existingUser.getId().equals(currentUserId)) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
        }
    }


    private long getNextUserId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
