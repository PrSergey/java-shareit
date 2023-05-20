package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    private static final String PATH = "/items";

    @SneakyThrows
    @Test
    void request_whenAutorizationNotProvided_thenBadRequest() {
        mvc.perform(get(PATH)).andExpect(status().isBadRequest());
        mvc.perform(post(PATH)).andExpect(status().isBadRequest());
        mvc.perform(patch(PATH)).andExpect(status().isMethodNotAllowed());
    }

    @SneakyThrows
    @Test
    void add() {
        ItemDto itemToSave = new ItemDto();
        long userId = 1L;
        when(itemService.add(userId, itemToSave))
                .thenReturn(itemToSave);

        String contentAsString = mvc.perform(post(PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToSave))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).add(userId, itemToSave);
        assertEquals(objectMapper.writeValueAsString(itemToSave), contentAsString);
    }

    @SneakyThrows
    @Test
    void getItem() {
        ItemResponseDto itemInMemory = new ItemResponseDto();
        long userId = 1L;
        long itemId = 1L;
        when(itemService.getItem(userId, itemId))
                .thenReturn(itemInMemory);

        mvc.perform(get(PATH + "/{itemId}", userId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).getItem(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getUsersItem_whenItemIsValid_thenReturnListWithItemResponseDto() {
        Long userId = 1L;
        String from = "2";
        String size = "2";
        List<ItemResponseDto> itemResponseDto = List.of(new ItemResponseDto());
        PageRequest pageRequest = PageRequest
                .of(Integer.parseInt(from) / Integer.parseInt(size), Integer.parseInt(size));
        when(itemService.getUsersItem(userId, pageRequest))
                .thenReturn(itemResponseDto);

        String itemAfterGet = mvc.perform(get(PATH)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getUsersItem(userId, pageRequest);
        assertEquals(objectMapper.writeValueAsString(itemResponseDto), itemAfterGet);
    }

    @SneakyThrows
    @Test
    void update() {
        ItemDto itemToSave = new ItemDto();
        long userId = 1L;
        long itemId = 1L;
        when(itemService.updateItem(userId, itemId, itemToSave))
                .thenReturn(itemToSave);

        String contentAsString = mvc.perform(patch(PATH + "/{itemId}", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemToSave))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).updateItem(userId, itemId, itemToSave);
        assertEquals(objectMapper.writeValueAsString(itemToSave), contentAsString);
    }

    @SneakyThrows
    @Test
    void searchItem() {
        Long userId = 1L;
        String from = "2";
        String size = "2";
        String text = "text";
        List<ItemDto> itemDto = List.of(new ItemDto());
        PageRequest pageRequest = PageRequest
                .of(Integer.parseInt(from) / Integer.parseInt(size), Integer.parseInt(size));
        when(itemService.searchItem(text, pageRequest))
                .thenReturn(itemDto);

        String contentAsString = mvc.perform(get(PATH + "/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItem(text, pageRequest);
        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
    }
//
//    @SneakyThrows
//    @Test
//    void addComment_whenCommentRequestDtoIsNotValid_thenNotFound() {
//        long itemId = 1L;
//        long userId = 1L;
//        CommentRequestDto commentForAdd = new CommentRequestDto();
//        commentForAdd.setText("");
//
//        mvc.perform(post(PATH + "/{itemId}/comment", itemId)
//                .header("X-Sharer-User-Id", userId)
//                .contentType("application/json")
//                .content(objectMapper.writeValueAsString(commentForAdd)))
//                .andExpect(status().isBadRequest());
//
//        verify(itemService, never()).saveComment(itemId, userId, commentForAdd);
//    }

    @SneakyThrows
    @Test
    void addComment_whenCommentRequestDtoIsValid_thenReturnComment() {
        long itemId = 1L;
        long userId = 1L;
        CommentRequestDto commentForAdd = new CommentRequestDto();
        commentForAdd.setText("Comment for item 1");
        CommentResponseDto commentForResponse = CommentResponseDto.builder().build();
        when(itemService.saveComment(itemId, userId, commentForAdd))
                .thenReturn(commentForResponse);

        String returnComment = mvc.perform(post(PATH + "/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentForAdd))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).saveComment(itemId, userId, commentForAdd);
        assertEquals(objectMapper.writeValueAsString(commentForResponse), returnComment);
    }
}