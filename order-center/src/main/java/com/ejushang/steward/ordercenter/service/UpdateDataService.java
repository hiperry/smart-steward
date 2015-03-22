package com.ejushang.steward.ordercenter.service;


import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.util.EJSDateUtils;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.common.util.NumberUtil;
import com.ejushang.steward.ordercenter.constant.OrderItemType;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.constant.PromotionType;
import com.ejushang.steward.ordercenter.constant.RefundStatus;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.util.CalculableOrderItem;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * User: liubin
 * Date: 14-4-14
 * Time: 上午9:19
 */
@Service
@Transactional
public class UpdateDataService {

    private static final Logger log = LoggerFactory.getLogger(UpdateDataService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderFeeService orderFeeService;

    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private OriginalOrderService originalOrderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;


    /**
     *
     */
    @Transactional
    public void updateOriginalOrderItemSkuZpLvXingDai() {
        //一些校验
        Map<String, String> productGiftSkuMap = new LinkedHashMap<String, String>();
        productGiftSkuMap.put("4895161311647", "DK012-1");
        productGiftSkuMap.put("4895161311654", "DK013-1");
        productGiftSkuMap.put("4895161311760", "DK014-1");
        String notFoundSku = "ZP旅行袋";
        String sql = "select original_order_id from steward.t_original_order oo join steward.t_original_order_item ooi on ooi.original_order_id = oo.id where processed = false and sku = ?";

        for(Map.Entry<String, String> entry : productGiftSkuMap.entrySet()) {
            String productSku = entry.getKey();
            String newSku = entry.getValue();
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, productSku);
            List<Integer> originalOrderIds = new ArrayList<Integer>(results.size());
            for(Map<String, Object> map : results) {
                originalOrderIds.add((Integer) (map.entrySet().iterator().next().getValue()));
            }
            if(originalOrderIds.isEmpty()) continue;
//            originalOrderIds = originalOrderIds.subList(0,20);

            StringBuilder updateSql = new StringBuilder("update t_original_order_item set sku='" + newSku + "' where sku='" + notFoundSku + "' and original_order_id in (");
            for(Integer originalOrderId : originalOrderIds) {
                updateSql.append("?,");
            }
            updateSql.replace(updateSql.length()-1, updateSql.length(), ")");
            int successCount = jdbcTemplate.update(updateSql.toString(), originalOrderIds.toArray());
            log.info(String.format("sku[%s]对应的赠品的从原sku[%s]改为[%s], 成功数量[%d]", productSku, notFoundSku, newSku, successCount));

        }

    }

    /**
     * 由于将原来的整单优惠拆成了平台优惠和店铺优惠,所以要重新计算8月订单金额
     * @throws Exception
     */
    @Transactional
    public void resetDiscountInfo() throws Exception {

        String startDateStr = "2014-08-01 00:00:00";
        Date startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd HH:mm:ss");

        List<OriginalOrder> originalOrderList = findOriginalOrderFullinfos(new PlatformType[]{PlatformType.JING_DONG, PlatformType.TAO_BAO},startDate,null);
        if(CollectionUtils.isEmpty(originalOrderList)){
            return;
        }

        Map<Integer, List<OrderItem>> orderItemPerOriginalOrderItemMap = new HashMap<Integer, List<OrderItem>>();
        Map<Integer, List<OrderItem>> orderItemPerOrderMap = new HashMap<Integer, List<OrderItem>>();
        Map<Integer, Order> changedOrderMap = new HashMap<Integer, Order>();

        String hql = "select oi from OrderItem oi join fetch oi.order o where oi.valid = true and o.buyTime >= ? and oi.originalOrderItemId is not null";
        List<OrderItem> allOrderItemList = generalDAO.query(hql, null, new Object[]{startDate});
        for(OrderItem orderItem : allOrderItemList) {
            List<OrderItem> originalForOrderItemList = orderItemPerOriginalOrderItemMap.get(orderItem.getOriginalOrderItemId());
            if(originalForOrderItemList == null) {
                originalForOrderItemList = new ArrayList<OrderItem>();
                orderItemPerOriginalOrderItemMap.put(orderItem.getOriginalOrderItemId(), originalForOrderItemList);
            }
            originalForOrderItemList.add(orderItem);

            List<OrderItem> orderItemPerOrderList = orderItemPerOrderMap.get(orderItem.getOrderId());
            if(orderItemPerOrderList == null) {
                orderItemPerOrderList = new ArrayList<OrderItem>();
                orderItemPerOrderMap.put(orderItem.getOrderId(), orderItemPerOrderList);
            }
            orderItemPerOrderList.add(orderItem);
        }

        Map<Integer, List<PromotionInfo>> promotionInfoMap = new HashMap<Integer, List<PromotionInfo>>();
        List<PromotionInfo> allPromotionInfoList = generalDAO.findAll(PromotionInfo.class);
        for(PromotionInfo promotionInfo : allPromotionInfoList) {
            List<PromotionInfo> promotionInfoList = promotionInfoMap.get(promotionInfo.getOriginalOrderId());
            if(promotionInfoList == null) {
                promotionInfoList = new ArrayList<PromotionInfo>();
                promotionInfoMap.put(promotionInfo.getOriginalOrderId(), promotionInfoList);
            }
            promotionInfoList.add(promotionInfo);
        }

        for(OriginalOrder originalOrder : originalOrderList){
            if(originalOrder.getOriginalOrderItemList().isEmpty()) {
                throw new StewardBusinessException(String.format("原始订单[id=%d]没有对应的原始订单项", originalOrder.getId()));
            }

            //订单部分不关心这两个字段
            if(originalOrder.getPlatformType().equals(PlatformType.JING_DONG)) {
                originalOrder.setDiscountFee(getDiscountFee(originalOrder.getPromotionInfoList()));
                originalOrder.setSelfDiscountFee(getSelfDiscountFee(originalOrder.getPromotionInfoList()));
            } else {
                //淘宝的原来discountFee字段存的就是selfDiscountFee
                originalOrder.setSelfDiscountFee(originalOrder.getDiscountFee());
                originalOrder.setDiscountFee(Money.valueOf(0d));
            }

            Money totalPayableFee = getTotalPayableFee(originalOrder);

            for(OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()){
                if(originalOrder.getPlatformType().equals(PlatformType.JING_DONG)) {
                    originalOrderItem.setPartMjzDiscount(getItemMjzDiscountFee(originalOrderItem.getPayableFee(),
                            totalPayableFee,originalOrder.getDiscountFee()));
                    originalOrderItem.setSelfPartMjzDiscount(getItemMjzDiscountFee(originalOrderItem.getPayableFee(),
                            totalPayableFee,originalOrder.getSelfDiscountFee()));
                } else {
                    //淘宝的原来discountFee字段存的就是selfDiscountFee
                    originalOrderItem.setSelfPartMjzDiscount(originalOrderItem.getPartMjzDiscount());
                    originalOrderItem.setPartMjzDiscount(Money.valueOf(0d));
                }

                originalOrderService.saveOriginalOrderItem(originalOrderItem);

                List<OrderItem> orderItemList = orderItemPerOriginalOrderItemMap.get(originalOrderItem.getId());
                if(orderItemList == null || orderItemList.isEmpty()) {
                    if(!paymentService.findByPlatformOrderNo(originalOrder.getPlatformType(), originalOrder.getPlatformOrderNo()).isEmpty()) {
                        //这个订单项是预收款
                        continue;
                    }
                    throw new StewardBusinessException(String.format("原始订单项[id=%d]没有对应的订单项", originalOrderItem.getId()));
                }

                if(orderItemList.size() == 1) {
                    OrderItem orderItem = orderItemList.get(0);
                    orderItem.setSharedDiscountFee(originalOrderItem.getPartMjzDiscount());
                    orderItem.setSelfSharedDiscountFee(originalOrderItem.getSelfPartMjzDiscount());

                    if(recalculateOrderItemFee(orderItem, orderItem.getOrder())) {
                        changedOrderMap.put(orderItem.getOrderId(), orderItem.getOrder());
                    }
                } else {

                    for (OrderItem orderItem : orderItemList) {
                        if (!orderItem.getType().equals(OrderItemType.MEALSET)) {
                            throw new StewardBusinessException(String.format("原始订单项[id=%d]对应的订单项是多个,但是订单项[id=%d]不是套餐订单项??", originalOrderItem.getId(),
                                    orderItem.getId()));
                        }
                        if(orderItem.getRealBuyCount() % originalOrderItem.getBuyCount() != 0) {
                            throw new StewardBusinessException(String.format("订单ID[%d],订单项ID[%d]的真实购买数量对原始订单项订货数量取模竟然不是0?这是套餐商品啊.",
                                    orderItem.getOrderId(), orderItem.getId()));
                        }

                        //根据当前套餐项的占比,计算分摊的优惠金额
                        BigDecimal mealsetItemTotalPrice = orderItem.getPrice().multiply(orderItem.getRealBuyCount() / originalOrderItem.getBuyCount()).getAmountWithBigDecimal();
                        BigDecimal percent = new BigDecimal(0d);
                        if (originalOrderItem.getPrice().getAmount() != 0d) {
                            percent = mealsetItemTotalPrice.divide(originalOrderItem.getPrice().getAmountWithBigDecimal(), 2, RoundingMode.HALF_UP);
                        }

                        Money sharedDiscountFee = Money.valueOf(originalOrderItem.getPartMjzDiscount().getAmountWithBigDecimal().setScale(2, RoundingMode.HALF_UP).multiply(percent).doubleValue());
                        Money selfSharedDiscountFee = Money.valueOf(originalOrderItem.getSelfPartMjzDiscount().getAmountWithBigDecimal().setScale(2, RoundingMode.HALF_UP).multiply(percent).doubleValue());
                        orderItem.setSharedDiscountFee(sharedDiscountFee);
                        orderItem.setSelfSharedDiscountFee(selfSharedDiscountFee);

                        if(recalculateOrderItemFee(orderItem, orderItem.getOrder())) {
                            changedOrderMap.put(orderItem.getOrderId(), orderItem.getOrder());
                        }
                    }
                }
            }

            originalOrderService.saveOriginalOrder(originalOrder);
        }

        for(Map.Entry<Integer, Order> entry : changedOrderMap.entrySet()) {
            Order order = entry.getValue();
            List<OrderItem> orderItems = orderItemPerOrderMap.get(order.getId());
            if(orderItems == null || orderItems.isEmpty()) {
                throw new StewardBusinessException(String.format("订单ID在重新计算金额的时候,没有从map中找到对应订单项", order.getId()));
            }

            recalculateOrderFee(order, orderItems);
        }

    }

    @Transactional
    public void checkIncorrectOrderItemAndOriginalOrderItem() throws Exception {
        String startDateStr = "2014-08-01 00:00:00";
        Date startDate = DateUtils.parseDate(startDateStr, "yyyy-MM-dd HH:mm:ss");

        List<OriginalOrder> originalOrderList = findOriginalOrderFullinfos(new PlatformType[]{PlatformType.JING_DONG, PlatformType.TAO_BAO},startDate,null);
        if(CollectionUtils.isEmpty(originalOrderList)){
            return;
        }

        Map<Integer, List<OrderItem>> orderItemPerOriginalOrderItemMap = new HashMap<Integer, List<OrderItem>>();
        Map<Integer, List<OrderItem>> orderItemPerOrderMap = new HashMap<Integer, List<OrderItem>>();
        Map<Integer, Order> changedOrderMap = new HashMap<Integer, Order>();
        List<String> errorInfoList = new ArrayList<String>();

        String hql = "select oi from OrderItem oi join fetch oi.order o where oi.valid = true and o.buyTime >= ? and oi.originalOrderItemId is not null";
        List<OrderItem> allOrderItemList = generalDAO.query(hql, null, new Object[]{startDate});
        for(OrderItem orderItem : allOrderItemList) {
            List<OrderItem> originalForOrderItemList = orderItemPerOriginalOrderItemMap.get(orderItem.getOriginalOrderItemId());
            if(originalForOrderItemList == null) {
                originalForOrderItemList = new ArrayList<OrderItem>();
                orderItemPerOriginalOrderItemMap.put(orderItem.getOriginalOrderItemId(), originalForOrderItemList);
            }
            originalForOrderItemList.add(orderItem);

            List<OrderItem> orderItemPerOrderList = orderItemPerOrderMap.get(orderItem.getOrderId());
            if(orderItemPerOrderList == null) {
                orderItemPerOrderList = new ArrayList<OrderItem>();
                orderItemPerOrderMap.put(orderItem.getOrderId(), orderItemPerOrderList);
            }
            orderItemPerOrderList.add(orderItem);
        }

        for(OriginalOrder originalOrder : originalOrderList){
            if(originalOrder.getOriginalOrderItemList().isEmpty()) {
                throw new StewardBusinessException(String.format("原始订单[id=%d]没有对应的原始订单项", originalOrder.getId()));
            }

            for(OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()){
                List<OrderItem> orderItemList = orderItemPerOriginalOrderItemMap.get(originalOrderItem.getId());
                if(orderItemList == null || orderItemList.isEmpty()) {
                    if(!paymentService.findByPlatformOrderNo(originalOrder.getPlatformType(), originalOrder.getPlatformOrderNo()).isEmpty()) {
                        //这个订单项是预收款
                        continue;
                    }
                    errorInfoList.add(String.format("原始订单项[id=%d]没有对应的订单项", originalOrderItem.getId()));
                    continue;
                }

                if(orderItemList.size() == 1) {

                } else {

                    for (OrderItem orderItem : orderItemList) {
                        if (!orderItem.getType().equals(OrderItemType.MEALSET)) {
                            errorInfoList.add(String.format("原始订单项[id=%d]对应的订单项是多个,但是订单项[id=%d]不是套餐订单项??", originalOrderItem.getId(),
                                    orderItem.getId()));
                            continue;
                        }
                        if(orderItem.getRealBuyCount() % originalOrderItem.getBuyCount() != 0) {
                            errorInfoList.add(String.format("订单ID[%d],订单项ID[%d]的真实购买数量对原始订单项订货数量取模竟然不是0?这是套餐商品啊.",
                                    orderItem.getOrderId(), orderItem.getId()));
                            continue;
                        }

                    }
                }

            }
        }

        log.info("errorInfoList size:" + errorInfoList.size());
        for(String errorInfo : errorInfoList) {
            log.info(errorInfo);
        }
    }


    /**
     * 解决因为手动拆单bug导致被设为valid为false的订单下面某些订单项valid不为false的bug
     * @throws Exception
     */
    @Transactional
    public void updateOrderItemValidToFalse() throws Exception {

        String hql = "select o from Order o left join o.orderItemList oi where o.valid = ? group by o.id";
        List<Order> orders = generalDAO.query(hql, null, false);

        int count = 0;

        for(Order order : orders) {
            for(OrderItem orderItem : order.getOrderItemList()) {
                if(orderItem.getValid()) {
                    orderItem.setValid(false);
                    generalDAO.saveOrUpdate(orderItem);
                    log.info("orderId: {}, generateType: {}, orderItemId: {} 已设置为false", new Object[]{order.getId(), order.getGenerateType(), orderItem.getId()});
                    count++;
                }
            }
        }
        log.info("总共修改订单项数量:{}", count);


    }


    /**
     * 删除订单
     * @throws Exception
     */
    @Transactional
    public void deleteOrder(List<Integer> orderIds) throws Exception {

        for(Integer orderId : orderIds) {

            Order order = generalDAO.get(Order.class, orderId);
            List<OrderItem> orderItemList = order.getOrderItemList();
            for(OrderItem orderItem : orderItemList) {
                Integer orderItemId = orderItem.getId();

                //判断订单项不能有关联数据
                Assert.isTrue(paymentService.findPAsByOrderItem(orderItemId).isEmpty());
                Assert.isTrue(refundService.findByOrderItemId(orderItemId).isEmpty());

                generalDAO.remove(orderItem);
            }

            //删除order关联数据
            generalDAO.remove(order.getInvoice());

            List<String> deleteOrderRelationDataList = Lists.newArrayList(
                    "delete from OrderApprove oa where oa.orderId = ?",
                    "delete from OrderHandleLog ohl where ohl.orderId = ?"
            );
            for(String hql : deleteOrderRelationDataList) {
                log.info("执行hql[{}], 删除数量[]", hql, generalDAO.update(hql, orderId));
            }
//            String hql = "delete from OrderDispose od where od.sourceIds like ? or od.targetIds like ?";
//            String param = "%" + String.valueOf(orderId) + ",%";
//            log.info("执行hql[{}], 删除数量[{}]", hql, generalDAO.update(hql, param, param));

            generalDAO.remove(order);
        }

    }


    /**
     * 线上退款时间应该是最后一次修改的时间
     */
    @Transactional
    public void updateRefundTime() {
        Search search = new Search(Refund.class).addFilterEqual("online", true).addFilterEqual("status", RefundStatus.SUCCESS);
        List<Refund> refundList = generalDAO.search(search);
        for(Refund refund : refundList) {
            if(refund.getOriginalRefund().getModified().after(refund.getRefundTime())) {
                log.info("退款[refundNo={},platformType={}]的退款时间发生变化, {} => {}", new Object[]{refund.getPlatformRefundNo(), refund.getPlatformType().getValue(),
                        EJSDateUtils.formatDate(refund.getRefundTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR),
                        EJSDateUtils.formatDate(refund.getOriginalRefund().getModified(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR)});
                refund.setRefundTime(refund.getOriginalRefund().getModified());
                generalDAO.saveOrUpdate(refund);
            }
        }

    }


    /**
         * 根据条件查询优惠详情
         * @param promotionInfo
         * @return
         */
    private List<PromotionInfo> findPromotionInfos(PromotionInfo promotionInfo){
        Search search = new Search(PromotionInfo.class);
        if (promotionInfo != null) {
            if (StringUtils.isNotBlank(promotionInfo.getPlatformOrderNo())) {
                search.addFilterEqual("platformOrderNo", promotionInfo.getPlatformOrderNo());
            }
            if (StringUtils.isNotBlank(promotionInfo.getSkuId())) {
                search.addFilterEqual("skuId", promotionInfo.getSkuId());
            }
            if(NumberUtil.isNotNullOrNotZero(promotionInfo.getOriginalOrderId())){
                search.addFilterEqual("originalOrderId",promotionInfo.getOriginalOrderId());
            }
        }
        List<PromotionInfo> promotionInfoList = generalDAO.search(search);
        return promotionInfoList;
    }

    /**
     * 根据条件查询唯一的优惠详情
     * @param promotionInfo
     * @return
     */
    private PromotionInfo getPromotionInfo(PromotionInfo promotionInfo){
        List<PromotionInfo> promotionInfoList = findPromotionInfos(promotionInfo);
        return CollectionUtils.isNotEmpty(promotionInfoList) ? promotionInfoList.get(0) : null;
    }


    /**
     * 获得订单总应付金额
     * @param originalOrder
     * @return
     */
    private Money getTotalPayableFee(OriginalOrder originalOrder){
        Money totalPayableFee = Money.valueOf(0);
        if(originalOrder == null || CollectionUtils.isEmpty(originalOrder.getOriginalOrderItemList())){
            return totalPayableFee;
        }

        for(OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()){
            totalPayableFee = totalPayableFee.add(originalOrderItem.getPayableFee());
        }
        return totalPayableFee;
    }

    /**
     * 计算获取分摊优惠金额
     * 计算公式：实付金额所占百分比 * 店铺优惠金额
     * @return
     */
    private Money getItemMjzDiscountFee(Money curPayableFee,Money totalPayableFee,Money discountFee){
        Money partMjzDiscountFee = Money.valueOf(0);
        if(curPayableFee == null || totalPayableFee == null || discountFee == null){
            return partMjzDiscountFee;
        }
        // 获得当前实付金额所占百分比
        BigDecimal paymentPercentBig = getPaymentPercent(curPayableFee,totalPayableFee);
        // 计算店铺优惠金额的分摊金额
        BigDecimal partMjzDiscountFeeBig = paymentPercentBig.multiply(new BigDecimal(discountFee.getAmount()));
        // 转为Money对象
        partMjzDiscountFee = Money.valueOf(partMjzDiscountFeeBig.doubleValue());

        return partMjzDiscountFee;
    }

    /**
     * 计算实付金额所占百分比
     * @return
     */
    private BigDecimal getPaymentPercent(Money curPayableFee,Money totalPayableFee){
        if(curPayableFee == null || totalPayableFee == null || totalPayableFee.getCent() == 0l){
            return BigDecimal.ZERO;
        }
        BigDecimal paymentPercentBig = new BigDecimal(curPayableFee.getCent()).divide(new BigDecimal(totalPayableFee.getCent()),4,BigDecimal.ROUND_HALF_UP);
        return paymentPercentBig;
    }

    /**
     * 获取整单优惠(需要分摊减去的优惠：100-店铺优惠，35-满返满送(返现))
     * @param promotionInfoList 订单优惠详情
     * @return
     */
    private Money getDiscountFee(List<PromotionInfo> promotionInfoList) {
        Money discountFee = Money.valueOf(0);
        if(promotionInfoList == null){
            return discountFee;
        }
        for(PromotionInfo promotionInfo : promotionInfoList){
            if(StringUtils.isBlank(promotionInfo.getSkuId())
                    && (StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_SHOUJIHONGBAO.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_JINGDOU.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_JINGDONGQUAN.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_LIPINKA.getDesc())
            )){
                discountFee = discountFee.add(promotionInfo.getDiscountFee());
            }
        }
        return discountFee;
    }

    /**
     * 获取整单优惠(需要分摊减去的优惠：100-店铺优惠，35-满返满送(返现))
     * @param promotionInfoList 订单优惠详情
     * @return
     */
    private Money getSelfDiscountFee(List<PromotionInfo> promotionInfoList) {
        Money selfDiscountFee = Money.valueOf(0);
        if(promotionInfoList == null){
            return selfDiscountFee;
        }
        for(PromotionInfo promotionInfo : promotionInfoList){
            if(StringUtils.isBlank(promotionInfo.getSkuId())
                    && (StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_TAOZHUANG.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_SHANTUAN.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_TUANGOU.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_DANPINCUXIAO.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_MANFANMANSONG.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_DIANPU.getDesc())
            )){
                selfDiscountFee = selfDiscountFee.add(promotionInfo.getDiscountFee());
            }
        }
        return selfDiscountFee;
    }


    private boolean recalculateOrderItemFee(CalculableOrderItem calculableOrderItem, Order order) {
        OrderItem orderItem = (OrderItem) calculableOrderItem;
        boolean changed = false;
        Money oldActualFee = orderItem.getActualFee();
        Money oldGoodsFee = orderItem.getGoodsFee();

        orderFeeService.calculateOrderItemFee(orderItem);

        if (!oldActualFee.equals(orderItem.getActualFee())) {
            changed = true;
            log.info("订单[orderNo={},platformType={}]中订单项[id={}]的平台结算金额发生变化, {} => {}", new Object[]{order.getOrderNo(),
                    order.getPlatformType().getValue(), orderItem.getId(), oldActualFee, orderItem.getActualFee()});
        }
        if (!oldGoodsFee.equals(orderItem.getGoodsFee())) {
            changed = true;
            log.info("订单[orderNo={},platformType={}]中订单项[id={}]的货款发生变化, {} => {}", new Object[]{order.getOrderNo(),
                    order.getPlatformType().getValue(), orderItem.getId(), oldGoodsFee, orderItem.getGoodsFee()});
        }

        generalDAO.saveOrUpdate(orderItem);

        return changed || order.getPlatformType().equals(PlatformType.TAO_BAO);
    }

    private boolean recalculateOrderFee(Order order, List<OrderItem> orderItemList) {
        boolean changed = false;
        Money oldActualFee = order.getActualFee();
        Money oldGoodsFee = order.getGoodsFee();

        Money sharedDiscountFee = Money.valueOf(0d);
        Money selfSharedDiscountFee = Money.valueOf(0d);
        for (OrderItem orderItem : orderItemList) {
            sharedDiscountFee = sharedDiscountFee.add(orderItem.getSharedDiscountFee());
            selfSharedDiscountFee = selfSharedDiscountFee.add(orderItem.getSelfSharedDiscountFee());
        }

        order.setSharedDiscountFee(sharedDiscountFee);
        order.setSelfSharedDiscountFee(selfSharedDiscountFee);

        orderFeeService.calculateOrderFee(order, orderItemList);

        if (!oldActualFee.equals(order.getActualFee())) {
            changed = true;
            log.info("订单[orderNo={},platformType={}]的平台结算金额发生变化, {} => {}", new Object[]{order.getOrderNo(),
                    order.getPlatformType().getValue(), oldActualFee, order.getActualFee()});
        }
        if (!oldGoodsFee.equals(order.getGoodsFee())) {
            changed = true;
            log.info("订单[orderNo={},platformType={}]的货款发生变化, {} => {}", new Object[]{order.getOrderNo(),
                    order.getPlatformType().getValue(), oldGoodsFee, order.getGoodsFee()});
        }

        generalDAO.saveOrUpdate(order);

        return changed;
    }


    /**
     * 获取订单、订单项、订单优惠信息
     * @param platformTypes
     * @param startTime
     * @param endTime
     * @return
     */
    private List<OriginalOrder> findOriginalOrderFullinfos(PlatformType[] platformTypes, Date startTime, Date endTime){
        StringBuilder hql = new StringBuilder("select ooi from OriginalOrderItem ooi join fetch ooi.originalOrder oo where oo.processed = true");
        List<Object> params = new ArrayList<Object>();
        if(platformTypes != null && platformTypes.length > 0) {
            hql.append(" and oo.platformType in ( ");
            for(PlatformType platformType : platformTypes) {
                hql.append("?,");
                params.add(platformType);
            }
            hql.replace(hql.length() - 1, hql.length(), ")");
        }
        if(startTime != null) {
            hql.append(" and oo.buyTime >= ? ");
            params.add(startTime);
        }
        if(endTime != null) {
            hql.append(" and oo.buyTime <= ? ");
            params.add(endTime);
        }

        List<OriginalOrderItem> allOriginalOrderItemList = generalDAO.query(hql.toString(), null, params.toArray());
        Map<Integer, OriginalOrder> originalOrderMap = new LinkedHashMap<Integer, OriginalOrder>();
        Map<Integer, List<OriginalOrderItem>> originalOrderItemMap = new LinkedHashMap<Integer, List<OriginalOrderItem>>();

        for(OriginalOrderItem originalOrderItem : allOriginalOrderItemList) {
            if(!originalOrderMap.containsKey(originalOrderItem.getOriginalOrderId())) {
                originalOrderMap.put(originalOrderItem.getOriginalOrderId(), originalOrderItem.getOriginalOrder());
            }

            List<OriginalOrderItem> originalOrderItemList = originalOrderItemMap.get(originalOrderItem.getOriginalOrderId());
            if(originalOrderItemList == null) {
                originalOrderItemList = new ArrayList<OriginalOrderItem>();
                originalOrderItemMap.put(originalOrderItem.getOriginalOrderId(), originalOrderItemList);
            }
            originalOrderItemList.add(originalOrderItem);
        }

        Map<Integer, List<PromotionInfo>> promotionInfoMap = new HashMap<Integer, List<PromotionInfo>>();
        List<PromotionInfo> allPromotionInfoList = generalDAO.findAll(PromotionInfo.class);
        for(PromotionInfo promotionInfo : allPromotionInfoList) {
            List<PromotionInfo> promotionInfoList = promotionInfoMap.get(promotionInfo.getOriginalOrderId());
            if(promotionInfoList == null) {
                promotionInfoList = new ArrayList<PromotionInfo>();
                promotionInfoMap.put(promotionInfo.getOriginalOrderId(), promotionInfoList);
            }
            promotionInfoList.add(promotionInfo);
        }


        List<OriginalOrder> originalOrderList = new ArrayList(originalOrderMap.values());
        if(!originalOrderList.isEmpty()){
            for(OriginalOrder originalOrderOri : originalOrderList){
                OriginalOrderItem originalOrderItem = new OriginalOrderItem();
                originalOrderItem.setOriginalOrderId(originalOrderOri.getId());
                List<OriginalOrderItem> originalOrderItemList = originalOrderItemMap.get(originalOrderOri.getId());
                if(CollectionUtils.isNotEmpty(originalOrderItemList)) {
                    originalOrderOri.setOriginalOrderItemList(originalOrderItemList);
                }

                List<PromotionInfo> promotionInfoList = promotionInfoMap.get(originalOrderOri.getId());
                originalOrderOri.setPromotionInfoList(promotionInfoList);
            }
        }
        return originalOrderList;
    }

}


