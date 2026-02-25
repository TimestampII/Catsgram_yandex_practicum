package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll(int from, int size, String sort) {
        // Преобразуем строку сортировки в enum
        SortOrder sortOrder = SortOrder.from(sort);

        return posts.values().stream()
                // Сортируем по дате создания
                .sorted((p1, p2) -> {
                    if (sortOrder == SortOrder.ASCENDING) {
                        return p1.getPostDate().compareTo(p2.getPostDate());
                    } else {
                        return p2.getPostDate().compareTo(p1.getPostDate());
                    }
                })
                // Пропускаем первые 'from' постов
                .skip(from)
                // Берем 'size' постов
                .limit(size)
                .collect(Collectors.toList());
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (userService.findById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);

        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Optional<Post> findById(long postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public enum SortOrder {
        ASCENDING, DESCENDING;

        // Преобразует строку в элемент перечисления
        public static SortOrder from(String order) {
            switch (order.toLowerCase()) {
                case "ascending":
                case "asc":
                    return ASCENDING;
                case "descending":
                case "desc":
                    return DESCENDING;
                default:
                    return null;
            }
        }
    }

}
