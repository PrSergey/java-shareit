package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    private final String path = "/requests";

    private final String authentificatedUser = "X-Sharer-User-Id";
//
//    @SneakyThrows
//    @Test
//    void add_whenItemRequestDtoIsNotValid_thenBadRequestTrow() {
//        ItemRequestDto itemRequestForAdd = new ItemRequestDto();
//        Long userId = 1L;
//        itemRequestForAdd.setDescription("");
//
//        mvc.perform(post(path)
//                .header(authentificatedUser, userId)
//                .contentType("application/json")
//                .content(objectMapper.writeValueAsString(itemRequestForAdd)))
//                .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, never()).save(userId, itemRequestForAdd);
//    }

    @SneakyThrows
    @Test
    void add_whenItemRequestDtoIsValid_thenReturnItemRequestDto() {
        ItemRequestDto itemRequestForAdd = new ItemRequestDto();
        Long userId = 1L;
        itemRequestForAdd.setDescription("description");
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        when(itemRequestService.save(userId, itemRequestForAdd))
                .thenReturn(itemRequestResponseDto);

        String itemRequestAfterAdd = mvc.perform(post(path)
                        .header(authentificatedUser, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestForAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).save(userId, itemRequestForAdd);
        assertEquals(itemRequestAfterAdd, objectMapper.writeValueAsString(itemRequestResponseDto));
    }

    @SneakyThrows
    @Test
    void getPersonalRequests() {
        long userId = 1L;
        List<ItemRequestResponseDto> itemRequestResponseDtos = List.of(new ItemRequestResponseDto());
        when(itemRequestService.getPersonalRequests(userId))
                .thenReturn(itemRequestResponseDtos);

        String itemRequests = mvc.perform(get(path)
                        .header(authentificatedUser, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getPersonalRequests(userId);
        assertEquals(itemRequests, objectMapper.writeValueAsString(itemRequestResponseDtos));
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        long userId = 1L;
        Long requestId = 1L;
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        when(itemRequestService.getItemRequestById(userId, requestId))
                .thenReturn(itemRequestResponseDto);

        String itemRequests = mvc.perform(get(path + "/{requestId}", requestId)
                        .header(authentificatedUser, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequestById(userId, requestId);
        assertEquals(itemRequests, objectMapper.writeValueAsString(itemRequestResponseDto));
    }

    @SneakyThrows
    @Test
    void getItemRequests() {
        Long userId = 1L;
        String from = "5";
        String size = "2";
        List<ItemRequestResponseDto> itemRequestResponseDtos = List.of(new ItemRequestResponseDto());
        PageRequest pageRequest = PageRequest
                .of(Integer.parseInt(from) / Integer.parseInt(size), Integer.parseInt(size));
        when(itemRequestService.getItemRequests(userId, pageRequest))
                .thenReturn(itemRequestResponseDtos);

        String itemRequest = mvc.perform(get(path + "/all")
                        .header(authentificatedUser, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getItemRequests(userId, pageRequest);
        assertEquals(itemRequest, objectMapper.writeValueAsString(itemRequestResponseDtos));
    }


}