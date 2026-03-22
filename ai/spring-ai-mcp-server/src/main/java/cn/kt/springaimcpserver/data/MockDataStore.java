package cn.kt.springaimcpserver.data;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MockDataStore {

    private final List<Market> markets = List.of(
            new Market("mkt-1", "成都农产品批发市场", "成都"),
            new Market("mkt-2", "重庆综合批发市场", "重庆"),
            new Market("mkt-3", "西安果蔬交易中心", "西安")
    );

    private final List<String> categories = List.of(
            "水果", "蔬菜", "肉类", "水产", "粮油", "干货"
    );

    private final List<Product> products = List.of(
            new Product("prd-1001", "红富士苹果", "10kg/箱", "水果"),
            new Product("prd-1002", "赣南脐橙", "9kg/箱", "水果"),
            new Product("prd-2001", "有机黄瓜", "5kg/箱", "蔬菜"),
            new Product("prd-2002", "新鲜西红柿", "5kg/箱", "蔬菜"),
            new Product("prd-3001", "冷鲜猪里脊", "20kg/箱", "肉类")
    );

    private final List<Supplier> suppliers = List.of(
            new Supplier("sup-1", "蜀农果业", "028-88888888", List.of("水果")),
            new Supplier("sup-2", "山城蔬菜基地", "023-66666666", List.of("蔬菜")),
            new Supplier("sup-3", "川味牧场", "028-77777777", List.of("肉类"))
    );

    private final List<Inventory> inventories = List.of(
            new Inventory("prd-1001", "mkt-1", 1200, List.of(
                    new InventoryBatch("B-20250318-01", "2026-03-18", 400),
                    new InventoryBatch("B-20250320-02", "2026-03-20", 800)
            )),
            new Inventory("prd-1001", "mkt-2", 600, List.of(
                    new InventoryBatch("B-20250319-01", "2026-03-19", 600)
            )),
            new Inventory("prd-2002", "mkt-1", 300, List.of(
                    new InventoryBatch("B-20250321-01", "2026-03-21", 300)
            )),
            new Inventory("prd-3001", "mkt-3", 150, List.of(
                    new InventoryBatch("B-20250315-02", "2026-03-15", 150)
            ))
    );

    public List<Market> markets() {
        return markets;
    }

    public List<String> categories() {
        return categories;
    }

    public List<Product> products() {
        return products;
    }

    public List<Supplier> suppliers() {
        return suppliers;
    }

    public List<Inventory> inventories() {
        return inventories;
    }

    public Map<String, Object> metadata() {
        return Map.of(
                "dataVersion", "v1",
                "generatedAt", "2026-03-22"
        );
    }

    public record Market(String id, String name, String location) {}

    public record Product(String id, String name, String spec, String category) {}

    public record Supplier(String id, String name, String phone, List<String> categories) {}

    public record Inventory(String productId, String marketId, int totalQuantity, List<InventoryBatch> batches) {}

    public record InventoryBatch(String batchNo, String receivedDate, int quantity) {}
}
