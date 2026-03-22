package cn.kt.springaimcpserver.service;

import cn.kt.springaimcpserver.data.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {

    private final MockDataStore dataStore;

    public ProductService(MockDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<MockDataStore.Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return dataStore.products();
        }
        String normalized = keyword.trim().toLowerCase(Locale.ROOT);
        return dataStore.products().stream()
                .filter(product -> containsIgnoreCase(product.name(), normalized)
                        || containsIgnoreCase(product.category(), normalized))
                .toList();
    }

    private static boolean containsIgnoreCase(String value, String keyword) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
