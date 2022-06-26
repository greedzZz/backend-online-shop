package greedzzz.megamarket.components.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShopUnit {
    private final String id;
    private final String name;
    private final String date;
    private final String parentId;
    private final String type;
    private Integer price;
    private final List<ShopUnit> children;
}
