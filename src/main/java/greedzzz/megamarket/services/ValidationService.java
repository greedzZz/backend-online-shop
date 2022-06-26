package greedzzz.megamarket.services;

import greedzzz.megamarket.components.schemas.ShopUnitImport;
import greedzzz.megamarket.components.schemas.ShopUnitImportRequest;
import greedzzz.megamarket.components.schemas.ShopUnitType;
import greedzzz.megamarket.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ValidationService {
    private final ProductService productService;

    @Autowired
    public ValidationService(ProductService productService) {
        this.productService = productService;
    }

    private boolean isISO8601(String date) {
        return date.matches("^([+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24:?00)([.,]\\d+(?!:))?)?(\\17[0-5]\\d([.,]\\d+)?)?([zZ]|([+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$");
    }

    public static boolean isUUID(String id) {
        return id.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    private boolean isValidId(String id, ShopUnitImport shopUnitImport, List<ShopUnitImport> items) {
        if (!isUUID(id)) return false;
        int count = 0;
        for (ShopUnitImport item : items) {
            String productId = item.getId();
            if (productId.equals(id)) count++;
            if (count > 1) return false;
        }
        List<Product> products = productService.findProducts();
        String name = shopUnitImport.getName();
        String type = shopUnitImport.getType();
        for (Product product : products)
            if (product.getId().equals(id))
                if (!(name.equals(product.getName()) && type.equals(product.getType().toString()))) return false;
        return true;
    }

    private boolean isValidParent(String parentId, List<ShopUnitImport> items) {
        if (!isUUID(parentId)) return false;
        for (ShopUnitImport item : items)
            if (item.getId().equals(parentId))
                return item.getType().equals("CATEGORY");
        List<Product> productsDB = productService.findProducts();
        for (Product product : productsDB)
            if (product.getId().equals(parentId))
                return product.getType() == ShopUnitType.CATEGORY;
        return false;
    }

    private boolean isShopUnitImport(ShopUnitImport shopUnitImport, List<ShopUnitImport> items) {
        String id = shopUnitImport.getId();
        String name = shopUnitImport.getName();
        String parentId = shopUnitImport.getParentId();
        ShopUnitType type = ShopUnitType.valueOf(shopUnitImport.getType());
        Integer price = shopUnitImport.getPrice();
        switch (type) {
            case OFFER:
                return (null != id && isValidId(id, shopUnitImport, items)) &&
                        (null != name) && (null == parentId || isValidParent(parentId, items)) &&
                        (price >= 0);
            case CATEGORY:
                return (null != id && isValidId(id, shopUnitImport, items)) &&
                        (null != name) && (null == parentId || isValidParent(parentId, items)) &&
                        (null == price);
            default:
                return false;
        }
    }

    @Transactional
    public boolean isShopUnitImportRequest(ShopUnitImportRequest request) {
        if (isISO8601(request.getUpdateDate())) {
            List<ShopUnitImport> items = request.getItems();
            for (ShopUnitImport item : items) {
                if (!isShopUnitImport(item, items)) return false;
            }
            return true;
        }
        return false;
    }
}
