package cn.kt.springaimcpserver.service;

import cn.kt.springaimcpserver.data.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SupplierService {

    private final MockDataStore dataStore;

    public SupplierService(MockDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Optional<MockDataStore.Supplier> findSupplier(String supplierId) {
        return dataStore.suppliers().stream()
                .filter(supplier -> supplier.id().equals(supplierId))
                .findFirst();
    }
}
