package greedzzz.megamarket.model;

import greedzzz.megamarket.components.schemas.ShopUnitType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Product parent;
    @Column
    private ShopUnitType type;
    @Column
    private Integer price;
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Product> children = new ArrayList<>();

    public Product(String id, String name, LocalDateTime date, ShopUnitType type, Integer price) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.price = price;
    }
}
