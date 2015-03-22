package com.ejushang.steward.ordercenter.constant;

import com.ejushang.steward.common.domain.util.ViewEnum;
import com.ejushang.steward.common.jackson.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 */
@JsonSerialize(using = EnumSerializer.class)
public enum FinancialOrderType implements ViewEnum {

    NORMAL_ORDER("正常订单"),
    EXCHANGE_ORDER("换货订单"),
    REPLENISHMENT_ORDER("补货订单"),
    POST_COVER("邮费补差"),
    SERVICE_COVER("服务补差"),
    ONLINE_REFUND("订单线上退款"),
    OFFLINE_REFUND("订单线下退款"),
    POST_COVER_REFUND("邮费补差退款"),
    SERVICE_COVER_REFUND("服务补差退款")
    ;


    public String value;

    FinancialOrderType(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }


    /**
     * 根据值取枚举
     * @param value
     * @return
     */
    public static FinancialOrderType enumValueOf(String value) {
        if(value == null) {
            return null;
        }
        for(FinancialOrderType enumValue : values()) {
            if(value.equalsIgnoreCase(enumValue.value)) {
                return enumValue;
            }
        }
        return null;
    }

}
