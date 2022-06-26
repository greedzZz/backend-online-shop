package greedzzz.megamarket.services;

import greedzzz.megamarket.components.schemas.ShopUnitImport;
import greedzzz.megamarket.components.schemas.ShopUnitImportRequest;
import greedzzz.megamarket.components.schemas.ShopUnitType;
import greedzzz.megamarket.model.Product;
import greedzzz.megamarket.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveProducts(ShopUnitImportRequest request) {
        List<ShopUnitImport> items = request.getItems();
        LocalDateTime date = getDate(request.getUpdateDate());
        List<Product> products = new ArrayList<>();
        List<String> parentIds = new ArrayList<>();
        for (ShopUnitImport item : items) {
            ShopUnitType type;
            if (item.getType().equals("OFFER")) type = ShopUnitType.OFFER;
            else type = ShopUnitType.CATEGORY;
            products.add(new Product(item.getId(), item.getName(), date, type, item.getPrice()));
            parentIds.add(item.getParentId());
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                String parentId = parentIds.get(i);
                if (null == parentId) product.setParent(null);
                else {
                    boolean found = false;
                    for (int j = 0; j < parentIds.size(); j++) {
                        Product potentialParent = products.get(j);
                        if (potentialParent.getId().equals(parentId)) {
                            product.setParent(potentialParent);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Optional<Product> potentialParent = repository.findById(parentId);
                        if (potentialParent.isPresent()) product.setParent(potentialParent.get());
                        else product.setParent(null);
                    }
                }
                products.set(i, product);
            }
        }
        if (products.size() != 0) updateDate(products.get(0));
        repository.saveAll(products);
    }

    @Transactional
    public Product findProduct(String id) {
        Optional<Product> product = repository.findById(id);
        return product.orElse(null);
    }

    @Transactional
    public List<Product> findProducts() {
        return repository.findAll();
    }

    @Transactional
    public void deleteProduct(String id) {
        repository.deleteById(id);
    }

    private void updateDate(Product product) {
        Product parent = product.getParent();
        if (null != parent) {
            parent.setDate(product.getDate());
            updateDate(parent);
        }
    }

    private LocalDateTime getDate(String strDate) {
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH));
    }
}
