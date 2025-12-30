package com.hiking.domain.hiking.valueobject;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 天气值对象
 * <p>
 * 值对象是 DDD 中的重要概念，它通过属性值来定义，
 * 没有唯一标识，是不可变的。
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {

    /**
     * 天气类型：晴天、多云、阴天、小雨、雨天
     */
    private String type;

    /**
     * 天气图标
     */
    private String icon;

    private Weather(String type, String icon) {
        this.type = type;
        this.icon = icon;
    }

    /**
     * 创建天气值对象的工厂方法
     */
    public static Weather of(String type, String icon) {
        if (type == null || type.isBlank()) {
            return null;
        }
        return new Weather(type, icon);
    }

    /**
     * 预定义天气类型
     */
    public static Weather sunny() {
        return new Weather("晴天", "☀️");
    }

    public static Weather cloudy() {
        return new Weather("多云", "⛅");
    }

    public static Weather overcast() {
        return new Weather("阴天", "☁️");
    }

    public static Weather lightRain() {
        return new Weather("小雨", "🌦️");
    }

    public static Weather rainy() {
        return new Weather("雨天", "🌧️");
    }

    @Override
    public String toString() {
        return icon + " " + type;
    }
}
