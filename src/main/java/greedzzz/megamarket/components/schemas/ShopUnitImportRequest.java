package greedzzz.megamarket.components.schemas;

import lombok.Data;

import java.util.List;

@Data
public class ShopUnitImportRequest {
    private final List<ShopUnitImport> items;
    private final String updateDate;
}
