package cn.kt.springaimcpserver.mcp;

import cn.kt.springaimcpserver.data.MockDataStore;
import cn.kt.springaimcpserver.service.InventoryService;
import cn.kt.springaimcpserver.service.ProductService;
import cn.kt.springaimcpserver.service.PurchasePlanService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WholesaleMarketTools {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final PurchasePlanService purchasePlanService;

    public WholesaleMarketTools(ProductService productService,
                                InventoryService inventoryService,
                                PurchasePlanService purchasePlanService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.purchasePlanService = purchasePlanService;
    }

    @McpTool(name = "search_products", description = "Search products by keyword.")
    public List<ProductSummary> searchProducts(
            @McpToolParam(description = "Keyword for product name or category", required = true)
            String keyword) {
        return productService.searchProducts(keyword).stream()
                .map(product -> new ProductSummary(
                        product.id(),
                        product.name(),
                        product.spec(),
                        product.category()))
                .toList();
    }

    @McpTool(name = "get_inventory", description = "Get inventory by product and market.")
    public InventorySummary getInventory(
            @McpToolParam(description = "Product ID", required = true) String productId,
            @McpToolParam(description = "Market ID", required = true) String marketId) {
        return inventoryService.findInventory(productId, marketId)
                .map(inventory -> new InventorySummary(
                        inventory.productId(),
                        inventory.marketId(),
                        inventory.totalQuantity(),
                        inventory.batches()))
                .orElseGet(() -> new InventorySummary(productId, marketId, 0, List.of()));
    }

    @McpTool(name = "create_purchase_plan", description = "Create a purchase plan for a product.")
    public PurchasePlanService.PurchasePlan createPurchasePlan(
            @McpToolParam(description = "Product ID", required = true) String productId,
            @McpToolParam(description = "Requested quantity", required = true) int quantity) {
        return purchasePlanService.createPlan(productId, quantity);
    }

    public record ProductSummary(String id, String name, String spec, String category) {}

    public record InventorySummary(String productId,
                                   String marketId,
                                   int totalQuantity,
                                   List<MockDataStore.InventoryBatch> batches) {}
}
