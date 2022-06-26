package greedzzz.megamarket.components.schemas;

import lombok.Data;

@Data
public class ShopUnitImport {
    private final String id;
    private final String name;
    private final String parentId;
    private final String type;
    private final Integer price;
}
