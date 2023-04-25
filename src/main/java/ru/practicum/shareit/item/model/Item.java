package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner", nullable = false)
    private Long owner;

    @Column(name = "name", nullable = false)
    @Size(max = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
