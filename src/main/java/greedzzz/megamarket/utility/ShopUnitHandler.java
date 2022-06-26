package greedzzz.megamarket.utility;

import greedzzz.megamarket.components.schemas.ShopUnit;
import greedzzz.megamarket.components.schemas.ShopUnitType;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ShopUnitHandler {
    public static void collectCategories(ShopUnit shopUnit, List<ShopUnit> categories) {
        for (ShopUnit child : shopUnit.getChildren()) {
            if (child.getType().equals(ShopUnitType.CATEGORY.toString())) {
                categories.add(child);
                collectCategories(child, categories);
            }
        }
    }

    public static Integer countPrice(ShopUnit category) {
        Queue<ShopUnit> queue = new LinkedList<>();
        queue.add(category);
        int sum = 0, count = 0;
        while (!queue.isEmpty()) {
            ShopUnit shopUnit = queue.poll();
            if (shopUnit.getType().equals(ShopUnitType.OFFER.toString())) {
                sum += shopUnit.getPrice();
                count++;
            } else {
                List<ShopUnit> children = shopUnit.getChildren();
                queue.addAll(children);
            }
        }
        return sum / count;
    }
}
