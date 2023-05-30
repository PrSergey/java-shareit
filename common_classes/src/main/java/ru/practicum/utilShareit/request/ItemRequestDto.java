package ru.practicum.utilShareit.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {

    @NotNull
    @NotBlank
    String description;

}
