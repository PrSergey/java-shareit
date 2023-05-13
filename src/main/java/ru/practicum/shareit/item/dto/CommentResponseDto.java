package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Data
public class CommentResponseDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}