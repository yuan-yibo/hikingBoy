package com.hiking.domain.share;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 分享策略工厂 - 管理所有平台的分享策略
 */
@Component
public class ShareStrategyFactory {
    
    private final Map<SocialPlatform, ShareStrategy> strategyMap;
    
    public ShareStrategyFactory(List<ShareStrategy> strategies) {
        this.strategyMap = new EnumMap<>(SocialPlatform.class);
        for (ShareStrategy strategy : strategies) {
            strategyMap.put(strategy.getPlatform(), strategy);
        }
    }
    
    /**
     * 根据平台类型获取对应的策略
     */
    public ShareStrategy getStrategy(SocialPlatform platform) {
        ShareStrategy strategy = strategyMap.get(platform);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for platform: " + platform);
        }
        return strategy;
    }
    
    /**
     * 根据平台代码获取对应的策略
     */
    public ShareStrategy getStrategy(String platformCode) {
        return getStrategy(SocialPlatform.fromCode(platformCode));
    }
}
