package cn.kt.springaimcpserver.service;

import cn.kt.springaimcpserver.data.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final MockDataStore dataStore;

    public InventoryService(MockDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<MockDataStore.Inventory> findInventory(String productId, String marketId) {
        return dataStore.inventories().stream()
                .filter(inv -> inv.productId().equals(productId) && inv.marketId().equals(marketId))
                .findFirst();
    }
}
