package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comment {

    private Long id;

    private String text;

    private Item item;

    private User authorName;

    private LocalDateTime created;

    public Comment(String text, Item item, User author, LocalDateTime dateCreated) {
        this.text = text;
        this.item = item;
        this.authorName = author;
        this.created = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
