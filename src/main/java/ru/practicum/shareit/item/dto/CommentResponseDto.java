package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}