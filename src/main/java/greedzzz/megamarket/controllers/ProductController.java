package greedzzz.megamarket.controllers;

import greedzzz.megamarket.components.schemas.Error;
import greedzzz.megamarket.components.schemas.ShopUnit;
import greedzzz.megamarket.components.schemas.ShopUnitImportRequest;
import greedzzz.megamarket.components.schemas.ShopUnitType;
import greedzzz.megamarket.model.Product;
import greedzzz.megamarket.services.ProductService;
import greedzzz.megamarket.services.ValidationService;
import greedzzz.megamarket.utility.Converter;
import greedzzz.megamarket.utility.ShopUnitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/imports")
    public ResponseEntity<Error> addProducts(@RequestBody ShopUnitImportRequest request) {
        ResponseEntity<Error> response;
        ValidationService validationService = new ValidationService(productService);
        if (validationService.isShopUnitImportRequest(request)) {
            productService.saveProducts(request);
            response = new ResponseEntity<>(new Error(200, "Success"), HttpStatus.OK);
        } else response = new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Error> deleteProduct(@PathVariable String id) {
        ResponseEntity<Error> response;
        if (ValidationService.isUUID(id)) {
            Product product = productService.findProduct(id);
            if (null != product) {
                productService.deleteProduct(id);
                response = new ResponseEntity<>(new Error(200, "OK"), HttpStatus.OK);
            } else response = new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.NOT_FOUND);
        } else response = new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        return response;
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<ShopUnit> getProduct(@PathVariable String id) {
        ResponseEntity<ShopUnit> response;
        if (ValidationService.isUUID(id)) {
            Product product = productService.findProduct(id);
            if (null != product) {
                ShopUnit shopUnit = Converter.toShopUnit(product);
                String shopUnitId = shopUnit.getId();
                List<ShopUnit> categories = new ArrayList<>();
                ShopUnit responseShopUnit = shopUnit;
                if (shopUnit.getType().equals(ShopUnitType.CATEGORY.toString())) {
                    ShopUnitHandler.collectCategories(shopUnit, categories);
                    categories.add(shopUnit);
                    for (ShopUnit category : categories) category.setPrice(ShopUnitHandler.countPrice(category));
                    for (ShopUnit category : categories) {
                        if (category.getId().equals(shopUnitId)) {
                            responseShopUnit = category;
                            break;
                        }
                    }
                }
                response = new ResponseEntity<>(responseShopUnit, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}
