package greedzzz.megamarket.utility;

import greedzzz.megamarket.components.schemas.ShopUnit;
import greedzzz.megamarket.components.schemas.ShopUnitType;
import greedzzz.megamarket.model.Product;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    public static ShopUnit toShopUnit(Product product) {
        String id = product.getId();
        String name = product.getName();
        String date = product.getDate().toString() + ":00.000Z";
        String parentId = null;
        if (null != product.getParent()) parentId = product.getParent().getId();
        String type = product.getType().toString();
        Integer price = product.getPrice();
        List<ShopUnit> children = null;
        if (!product.getChildren().isEmpty()) children = Converter.toShopUnitList(product.getChildren());
        return new ShopUnit(id, name, date, parentId, type, price, children);
    }

    public static List<ShopUnit> toShopUnitList(List<Product> products) {
        List<ShopUnit> shopUnitList = new ArrayList<>();
        for (Product product : products) shopUnitList.add(Converter.toShopUnit(product));
        return shopUnitList;
    }
}
