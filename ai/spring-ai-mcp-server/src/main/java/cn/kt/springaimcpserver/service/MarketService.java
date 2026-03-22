package cn.kt.springaimcpserver.service;

import cn.kt.springaimcpserver.data.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketService {

    private final MockDataStore dataStore;

    public MarketService(MockDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<MockDataStore.Market> listMarkets() {
        return dataStore.markets();
    }
}
