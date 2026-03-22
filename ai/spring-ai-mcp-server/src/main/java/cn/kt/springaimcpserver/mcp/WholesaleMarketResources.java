package cn.kt.springaimcpserver.mcp;

import cn.kt.springaimcpserver.data.MockDataStore;
import cn.kt.springaimcpserver.service.MarketService;
import cn.kt.springaimcpserver.service.SupplierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WholesaleMarketResources {

    private final MarketService marketService;
    private final SupplierService supplierService;
    private final MockDataStore dataStore;
    private final ObjectMapper objectMapper;

    public WholesaleMarketResources(MarketService marketService,
                                    SupplierService supplierService,
                                    MockDataStore dataStore,
                                    ObjectMapper objectMapper) {
        this.marketService = marketService;
        this.supplierService = supplierService;
        this.dataStore = dataStore;
        this.objectMapper = objectMapper;
    }

    @McpResource(uri = "resource://markets", name = "Markets", description = "Market list")
    public ReadResourceResult markets() throws JsonProcessingException {
        return jsonResource("resource://markets", marketService.listMarkets());
    }

    @McpResource(uri = "resource://product-categories", name = "Product Categories", description = "Category list")
    public ReadResourceResult categories() throws JsonProcessingException {
        return jsonResource("resource://product-categories", dataStore.categories());
    }

    @McpResource(uri = "resource://supplier/{id}", name = "Supplier", description = "Supplier detail by id")
    public ReadResourceResult supplier(String id) throws JsonProcessingException {
        Object result = supplierService.findSupplier(id)
                .<Object>map(supplier -> supplier)
                .orElseGet(() -> new ErrorMessage("Supplier not found", id));
        return jsonResource("resource://supplier/" + id, result);
    }

    private ReadResourceResult jsonResource(String uri, Object payload) throws JsonProcessingException {
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
        return new ReadResourceResult(List.of(
                new TextResourceContents(uri, "application/json", json)
        ));
    }

    public record ErrorMessage(String message, String id) {}
}
