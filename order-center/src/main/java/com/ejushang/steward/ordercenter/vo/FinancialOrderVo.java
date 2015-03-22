package com.ejushang.steward.ordercenter.vo;

import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.ordercenter.constant.FinancialOrderType;
import com.ejushang.steward.ordercenter.constant.OrderStatus;
import com.ejushang.steward.ordercenter.constant.PlatformType;

import java.util.Date;

/**
 * User:moon
 * Date: 14-6-4
 * Time: 下午4:57
 */
public class FinancialOrderVo {

    /**订单来自那个平台（如天猫，京东）*/
    private PlatformType platformType;

    private FinancialOrderType financialOrderType;
    /**店铺名称*/
    private String shopName;
    /**外部系统的订单号（如天猫）*/
    private String platformOrderNo;
    /**订单编号*/
    private String orderNo;
    /**订单id*/
    private Integer orderId;

    private OrderStatus status;

    private String repoName;

    //平台分摊优惠金额
    private Money sharedDiscountFee = Money.valueOf(0d);

    //店铺分摊优惠金额
    private Money selfSharedDiscountFee = Money.valueOf(0d);

    private String platformRefundNo;

    private Date refundTime;

    /**下单时间*/
    private Date buyTime;
    /**付款时间*/
    private Date payTime;
    /**打印时间*/
    private Date printTime;

    /**买家ID，即买家的淘宝号*/
    private String buyerId;
    /**收货人的姓名*/
    private String receiverName;
    /**收货人手机*/
    private String receiverPhone;
    /**收货人电话*/
    private String receiverMobile;
    /**收货人地址*/
    private String receiverAddress;
    /**物流公司*/
    private String shippingComp;
    /**物流编号*/
    private String shippingNo;

    private FinancialOrderItemVo orderItemVo;

    /**补差关联外部平台订单编号*/
    private String paymentAllocationPlatformOrderNo;

    /**补差关联智库城订单编号*/
    private String paymentAllocationOrderNo;

    /** 预收款ID,只是生成报表的时候用到,不显示到前端 **/
    private Integer paymentId;

    /** 预收款分配ID,只是生成报表的时候用到,不显示到前端 **/
    private Integer paymentAllocationId;

    public String getPlatformRefundNo() {
        return platformRefundNo;
    }

    public void setPlatformRefundNo(String platformRefundNo) {
        this.platformRefundNo = platformRefundNo;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public FinancialOrderType getFinancialOrderType() {
        return financialOrderType;
    }

    public void setFinancialOrderType(FinancialOrderType financialOrderType) {
        this.financialOrderType = financialOrderType;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPlatformOrderNo() {
        return platformOrderNo;
    }

    public void setPlatformOrderNo(String platformOrderNo) {
        this.platformOrderNo = platformOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public Money getSharedDiscountFee() {
        return sharedDiscountFee;
    }

    public void setSharedDiscountFee(Money sharedDiscountFee) {
        this.sharedDiscountFee = sharedDiscountFee;
    }

    public Money getSelfSharedDiscountFee() {
        return selfSharedDiscountFee;
    }

    public void setSelfSharedDiscountFee(Money selfSharedDiscountFee) {
        this.selfSharedDiscountFee = selfSharedDiscountFee;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getPrintTime() {
        return printTime;
    }

    public void setPrintTime(Date printTime) {
        this.printTime = printTime;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getShippingComp() {
        return shippingComp;
    }

    public void setShippingComp(String shippingComp) {
        this.shippingComp = shippingComp;
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }

    public FinancialOrderItemVo getOrderItemVo() {
        return orderItemVo;
    }

    public void setOrderItemVo(FinancialOrderItemVo orderItemVo) {
        this.orderItemVo = orderItemVo;
    }

    public String getPaymentAllocationPlatformOrderNo() {
        return paymentAllocationPlatformOrderNo;
    }

    public void setPaymentAllocationPlatformOrderNo(String paymentAllocationPlatformOrderNo) {
        this.paymentAllocationPlatformOrderNo = paymentAllocationPlatformOrderNo;
    }

    public String getPaymentAllocationOrderNo() {
        return paymentAllocationOrderNo;
    }

    public void setPaymentAllocationOrderNo(String paymentAllocationOrderNo) {
        this.paymentAllocationOrderNo = paymentAllocationOrderNo;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPaymentAllocationId() {
        return paymentAllocationId;
    }

    public void setPaymentAllocationId(Integer paymentAllocationId) {
        this.paymentAllocationId = paymentAllocationId;
    }
}
