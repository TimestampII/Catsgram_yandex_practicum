package ru.yandex.practicum.catsgram.model;
import lombok.EqualsAndHashCode;
import java.time.Instant;
import java.util.Objects;

@EqualsAndHashCode(of ={"id"})
public class Image {
    private Long id;
    private Long postId;
    private String originalFileName;
    private String filePath;

}
