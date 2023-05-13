package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(Long id, PageRequest pageRequest);

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE LOWER(it.name) LIKE LOWER(concat('%',?1, '%')) " +
            "OR LOWER(it.description) LIKE LOWER(concat('%',?1, '%')) " +
            "AND it.available = true")
    List<Item> itemWithText(String text, PageRequest pageRequest);

    List<Item> findAllByRequest_id(Long requestId);

    List<Item> findAllByRequest_IdIn(List<Long> requestsId);


}
