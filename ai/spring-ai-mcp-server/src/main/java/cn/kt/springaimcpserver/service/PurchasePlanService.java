package cn.kt.springaimcpserver.service;

import org.springframework.stereotype.Service;

@Service
public class PurchasePlanService {

    public PurchasePlan createPlan(String productId, int quantity) {
        int recommended = Math.max(quantity, 100);
        String reason = quantity < 100
                ? "需求量较低，建议统一补货到 100 以降低物流成本"
                : "按需求量生成补货建议";
        return new PurchasePlan(productId, recommended, reason);
    }

    public record PurchasePlan(String productId, int recommendedQuantity, String reason) {}
}
