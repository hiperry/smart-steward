package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.domain.Employee;
import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.util.*;
import com.ejushang.steward.ordercenter.constant.*;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.util.OrderUtil;
import com.ejushang.steward.ordercenter.vo.FinancialOrderItemVo;
import com.ejushang.steward.ordercenter.vo.FinancialOrderVo;
import com.ejushang.steward.ordercenter.vo.FinancialQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User:moon
 * Date: 14-6-17
 * Time: 下午2:47
 */
@Service
@Transactional
public class FinancialService {

    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private OrderApproveService orderApproveService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderFeeService orderFeeService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 得到有关订单的信息
     * @param itemRow
     * @param financialOrderVo
     */
    private void exportFinancialOrderVo2Excel(Row itemRow, FinancialOrderVo financialOrderVo) {
        int startCellIndex = 0;

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getPlatformType().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getFinancialOrderType().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getShopName());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getPlatformOrderNo());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getOrderNo());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getStatus() == null ? null : financialOrderVo.getStatus().getValue());//订单状态
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getRepoName());//仓库

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getSharedDiscountFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getSelfSharedDiscountFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getPlatformRefundNo());
        PoiUtil.createCell(itemRow, startCellIndex++, EJSDateUtils.formatDate(financialOrderVo.getRefundTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR_WITHOUT_ZERO));
        PoiUtil.createCell(itemRow, startCellIndex++, EJSDateUtils.formatDate(financialOrderVo.getBuyTime(),EJSDateUtils.DateFormatType.DATE_FORMAT_STR_WITHOUT_ZERO));
        PoiUtil.createCell(itemRow, startCellIndex++, EJSDateUtils.formatDate(financialOrderVo.getPayTime(),EJSDateUtils.DateFormatType.DATE_FORMAT_STR_WITHOUT_ZERO));
        PoiUtil.createCell(itemRow, startCellIndex++, EJSDateUtils.formatDate(financialOrderVo.getPrintTime(),EJSDateUtils.DateFormatType.DATE_FORMAT_STR_WITHOUT_ZERO));

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getBuyerId());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getReceiverName());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getReceiverPhone());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getReceiverMobile());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getReceiverAddress());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getShippingComp());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getShippingNo());


        //订单项信息
        FinancialOrderItemVo financialOrderItemVo = financialOrderVo.getOrderItemVo();
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getPlatformSubOrderNo());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getType());  //订单项类型
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getReturnStatus() == null ? null : financialOrderItemVo.getReturnStatus().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getOfflineReturnStatus() == null ? null : financialOrderItemVo.getOfflineReturnStatus().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getExchangedGoods());

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getProductCode());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getProductName());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getSpecInfo());  //规格
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getProductSku());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getOuterSku());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getBrandName());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getCateName());

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getPrice().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getDiscountPrice().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getBuyCount());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getDiscountFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getSharedDiscountFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getSelfSharedDiscountFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getSharedPostFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getActualFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getGoodsFee().toString());

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getRefundFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getActualRefundFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getOfflineRefundFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getReturnPostFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getReturnPostPayer() == null ? null : financialOrderItemVo.getReturnPostPayer().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getOfflineReturnPostFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getOfflineReturnPostPayer() == null ? null : financialOrderItemVo.getOfflineReturnPostPayer().getValue());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getExchangePostFee().toString());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderItemVo.getExchangePostPayer() == null ? null : financialOrderItemVo.getExchangePostPayer().getValue());

        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getPaymentAllocationPlatformOrderNo());
        PoiUtil.createCell(itemRow, startCellIndex++, financialOrderVo.getPaymentAllocationOrderNo());
    }


    private void createExcelTitle(Sheet sheet) {
        int cellIndex = 0;
        Row row = sheet.createRow(0);
        PoiUtil.createCell(row, cellIndex++, "平台类型");
        PoiUtil.createCell(row, cellIndex++, "数据类型");
        PoiUtil.createCell(row, cellIndex++, "店铺名称");
        PoiUtil.createCell(row, cellIndex++, "外部平台订单编号");
        PoiUtil.createCell(row, cellIndex++, "智库城订单编号");
        PoiUtil.createCell(row, cellIndex++, "订单状态");
        PoiUtil.createCell(row, cellIndex++, "仓库");
        PoiUtil.createCell(row, cellIndex++, "平台整单优惠金额");
        PoiUtil.createCell(row, cellIndex++, "店铺整单优惠金额");
        PoiUtil.createCell(row, cellIndex++, "退款单号");
        PoiUtil.createCell(row, cellIndex++, "退款时间");

        PoiUtil.createCell(row, cellIndex++, "下单时间");
        PoiUtil.createCell(row, cellIndex++, "付款时间");
        PoiUtil.createCell(row, cellIndex++, "打印时间");
        PoiUtil.createCell(row, cellIndex++, "买家ID");
        PoiUtil.createCell(row, cellIndex++, "收货人");
        PoiUtil.createCell(row, cellIndex++, "收货电话");
        PoiUtil.createCell(row, cellIndex++, "收货手机");
        PoiUtil.createCell(row, cellIndex++, "收货地址");

        PoiUtil.createCell(row, cellIndex++, "快递公司");
        PoiUtil.createCell(row, cellIndex++, "快递单号");
        PoiUtil.createCell(row, cellIndex++, "外部平台订单项编号");
        PoiUtil.createCell(row, cellIndex++, "订单项类型");
        PoiUtil.createCell(row, cellIndex++, "线上退货状态");
        PoiUtil.createCell(row, cellIndex++, "线下退货状态");
        PoiUtil.createCell(row, cellIndex++, "售前换货状态");
        PoiUtil.createCell(row, cellIndex++, "商品编号");
        PoiUtil.createCell(row, cellIndex++, "商品名称");
        PoiUtil.createCell(row, cellIndex++, "商品规格");

        PoiUtil.createCell(row, cellIndex++, "sku");
        PoiUtil.createCell(row, cellIndex++, "外部平台sku");//
        PoiUtil.createCell(row, cellIndex++, "品牌");
        PoiUtil.createCell(row, cellIndex++, "类别");
        PoiUtil.createCell(row, cellIndex++, "原价（一口价）");
        PoiUtil.createCell(row, cellIndex++, "促销价");
        PoiUtil.createCell(row, cellIndex++, "订货数量");
        PoiUtil.createCell(row, cellIndex++, "订单项优惠金额");
        PoiUtil.createCell(row, cellIndex++, "平台分摊优惠金额");
        PoiUtil.createCell(row, cellIndex++, "店铺分摊优惠金额");

        PoiUtil.createCell(row, cellIndex++, "分摊邮费");
        PoiUtil.createCell(row, cellIndex++, "平台结算金额");
        PoiUtil.createCell(row, cellIndex++, "货款");
        PoiUtil.createCell(row, cellIndex++, "线上账面退款金额");
        PoiUtil.createCell(row, cellIndex++, "线上实际退款金额");
        PoiUtil.createCell(row, cellIndex++, "线下退款金额");
        PoiUtil.createCell(row, cellIndex++, "线上退货邮费");

        PoiUtil.createCell(row, cellIndex++, "线上退货邮费承担方");
        PoiUtil.createCell(row, cellIndex++, "线下退货邮费");
        PoiUtil.createCell(row, cellIndex++, "线下退货邮费承担方");
        PoiUtil.createCell(row, cellIndex++, "线下换货邮费");
        PoiUtil.createCell(row, cellIndex++, "线下换货邮费承担方");
        PoiUtil.createCell(row, cellIndex++, "补差关联外部平台订单编号");
        PoiUtil.createCell(row, cellIndex++, "补差关联智库城订单编号");

    }

    /**
     * 导出财务报表
     * @param financialQueryVo
     * @return
     */
    @Transactional(readOnly = true)
    public Workbook reportOrderItem(FinancialQueryVo financialQueryVo) {

        //将打印日期和发货日期的数据放到map里,key为订单ID
        List<OrderApprove> printedOrderApproveList = orderApproveService.findByOrderStatus(OrderStatus.PRINTED);
        Map<Integer, OrderApprove> printedOrderApproveMap = new HashMap<Integer, OrderApprove>();
        for(OrderApprove orderApprove : printedOrderApproveList) {
            printedOrderApproveMap.put(orderApprove.getOrderId(), orderApprove);
        }
        List<OrderApprove> invoicedOrderApproveList = orderApproveService.findByOrderStatus(OrderStatus.INVOICED);
        Map<Integer, OrderApprove> invoicedOrderApproveMap = new HashMap<Integer, OrderApprove>();
        for(OrderApprove orderApprove : invoicedOrderApproveList) {
            invoicedOrderApproveMap.put(orderApprove.getOrderId(), orderApprove);
        }


        //查询财务数据
        Object[] results = findFinancialDataByQuery(financialQueryVo);
        //将财务数据组合为显示的VO
        List<FinancialOrderVo> financialOrderVoList = assembleFinancialOrderVo(results, printedOrderApproveMap, invoicedOrderApproveMap);

        //生成excel
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        createExcelTitle(sheet);
        int rowIndex = 1;//从第二行开始，一行放title
        for (FinancialOrderVo financialOrderVo : financialOrderVoList) {
            Row row = sheet.createRow(rowIndex++);
            exportFinancialOrderVo2Excel(row, financialOrderVo);
        }
        return workbook;
    }


    /**
     * 根据条件查询财务数据
     * @param financialQueryVo [发货订单项list, 退款list, 预收款list]
     * @return
     */
    private Object[] findFinancialDataByQuery(FinancialQueryVo financialQueryVo) {

        StringBuilder orderItemHql = new StringBuilder("select item_ from OrderItem item_  join fetch item_.order order_ where order_.valid=true and order_.type!=:orderType ");
        StringBuilder refundHql = new StringBuilder("select r from Refund r  left join fetch r.orderItem item_ left join fetch item_.order order_ where order_.valid=true and order_.type!=:orderType ");
        StringBuilder paymentHql = new StringBuilder("select p from Payment p where p.type in(:postCoverType, :serviceCoverType) ");
        Map<String, Object> orderItemParameters = new HashMap<String, Object>();
        Map<String, Object> refundParameters = new HashMap<String, Object>();
        Map<String, Object> paymentParameters = new HashMap<String, Object>();
        orderItemParameters.put("orderType", OrderType.CHEAT);
        refundParameters.put("orderType", OrderType.CHEAT);
        paymentParameters.put("postCoverType", PaymentType.POST_COVER);
        paymentParameters.put("serviceCoverType", PaymentType.SERVICE_COVER);

        //下单时间丶付款时间
        String searchTimeType = financialQueryVo.getSearchTimeType();
        if(!StringUtils.isBlank(searchTimeType)){
            if(searchTimeType.equalsIgnoreCase("payTime") || searchTimeType.equalsIgnoreCase("buyTime")){
                if (!StringUtils.isBlank(financialQueryVo.getStartTime())) {
                    orderItemHql.append(" and order_.").append(searchTimeType).append(">=:minTime ");
                    orderItemParameters.put("minTime", EJSDateUtils.parseDate(financialQueryVo.getStartTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));

                    refundHql.append(" and r.refundTime>=:minTime ");
                    refundParameters.put("minTime", EJSDateUtils.parseDate(financialQueryVo.getStartTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));

                    paymentHql.append(" and p.").append(searchTimeType).append(">=:minTime ");
                    paymentParameters.put("minTime", EJSDateUtils.parseDate(financialQueryVo.getStartTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));
                }
                if (!StringUtils.isBlank(financialQueryVo.getEndTime())) {

                    orderItemHql.append(" and order_.").append(searchTimeType).append("<=:maxTime ");
                    orderItemParameters.put("maxTime", EJSDateUtils.parseDate(financialQueryVo.getEndTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));

                    refundHql.append(" and r.refundTime<=:maxTime ");
                    refundParameters.put("maxTime", EJSDateUtils.parseDate(financialQueryVo.getEndTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));

                    paymentHql.append(" and p.").append(searchTimeType).append("<=:maxTime ");
                    paymentParameters.put("maxTime", EJSDateUtils.parseDate(financialQueryVo.getEndTime(), EJSDateUtils.DateFormatType.DATE_FORMAT_STR));
                }
            }

        }
        if (!StringUtils.isBlank(financialQueryVo.getPlatformType())) {
            orderItemHql.append(" and order_.platformType=:platformType ");
            orderItemParameters.put("platformType", PlatformType.valueOf(financialQueryVo.getPlatformType()));

            refundHql.append(" and r.platformType=:platformType ");
            refundParameters.put("platformType", PlatformType.valueOf(financialQueryVo.getPlatformType()));

            paymentHql.append(" and p.platformType=:platformType ");
            paymentParameters.put("platformType", PlatformType.valueOf(financialQueryVo.getPlatformType()));
        }
        if (!NumberUtil.isNullOrZero(financialQueryVo.getShopId())) {
            orderItemHql.append(" and order_.shopId=:shopId ");
            orderItemParameters.put("shopId", financialQueryVo.getShopId());

            refundHql.append(" and r.shopId=:shopId ");
            refundParameters.put("shopId", financialQueryVo.getShopId());

            paymentHql.append(" and p.shopId=:shopId ");
            paymentParameters.put("shopId", financialQueryVo.getShopId());
        }

        //仓库权限
        Employee employee = SessionUtils.getEmployee();
        if (employee != null && employee.isRepositoryEmployee()) {     //仓库人员只能查看自己管理的仓库的日志
            orderItemHql.append(" and order_.repoId in ( select repo_.repositoryId from RepositoryCharger repo_ where repo_.chargerId= ").append(employee.getId()).append(")");

        } else {
            if (!NumberUtil.isNullOrZero(financialQueryVo.getRepoId())) {
                orderItemHql.append(" and order_.repoId=:repoId ");
                orderItemParameters.put("repoId", financialQueryVo.getRepoId());
            }
        }

        orderItemHql.append(" order by order_.buyTime desc ");
        refundHql.append(" order by r.refundTime desc ");
        paymentHql.append(" order by p.buyTime desc ");

        Query orderItemQuery = generalDAO.getSession().createQuery(orderItemHql.toString());
        Query refundQuery = generalDAO.getSession().createQuery(refundHql.toString());
        Query paymentQuery = generalDAO.getSession().createQuery(paymentHql.toString());

        for(Map.Entry<String, Object> entry : orderItemParameters.entrySet()) {
            orderItemQuery.setParameter(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, Object> entry : refundParameters.entrySet()) {
            refundQuery.setParameter(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, Object> entry : paymentParameters.entrySet()) {
            paymentQuery.setParameter(entry.getKey(), entry.getValue());
        }

        List<OrderItem> orderItems = orderItemQuery.list();
        List<Refund> refunds = refundQuery.list();
        List<Payment> payments = paymentQuery.list();

        return new Object[]{orderItems, refunds, payments};


    }

    /**
     * //将财务数据组合为显示的VO
     * @param results
     * @param printedOrderApproveMap
     * @param invoicedOrderApproveMap
     * @return
     */
    private List<FinancialOrderVo> assembleFinancialOrderVo(Object[] results, Map<Integer, OrderApprove> printedOrderApproveMap, Map<Integer, OrderApprove> invoicedOrderApproveMap) {
        List<OrderItem> orderItems = (List<OrderItem>)results[0];
        List<Refund> refunds = (List<Refund>)results[1];
        List<Payment> payments = (List<Payment>)results[2];

        //财务VO list
        List<FinancialOrderVo> financialOrderVoList = new ArrayList<FinancialOrderVo>();
        //key为订单项ID, value为对应的财务VO
        Map<Integer, FinancialOrderVo> financialOrderItemVoMap = new HashMap<Integer, FinancialOrderVo>();
        //key为预收款ID, value为对应的财务VO list(因为如果预收款已分配,每个预收款分配记录为一个VO)
        Map<Integer, List<FinancialOrderVo>> financialPaymentOrAllocationVoMap = new HashMap<Integer, List<FinancialOrderVo>>();

        for(OrderItem orderItem : orderItems) {
            //首先为订单项创建VO数据
            FinancialOrderVo financialOrderVo = createByOrderItem(orderItem.getOrder(), orderItem, printedOrderApproveMap, invoicedOrderApproveMap);
            financialOrderVoList.add(financialOrderVo);
            if(financialOrderItemVoMap.put(financialOrderVo.getOrderItemVo().getId(), financialOrderVo) != null) {
                throw new StewardBusinessException("put的时候发现financialOrderItemVoMap对应的订单项Id的key已经存在");
            }
        }

        for(Payment payment : payments) {
            //为预收款创建VO
            List<FinancialOrderVo> financialPaymentOrAllocationVoList = createByPayment(financialOrderVoList, payment);

            if(financialPaymentOrAllocationVoMap.put(payment.getId(), financialPaymentOrAllocationVoList) != null) {
                throw new StewardBusinessException("put的时候发现financialOrderItemVoMap对应的订单项Id的key已经存在");
            }
        }

        for(Refund refund : refunds) {

            switch(refund.getType()) {
                case ORDER: {
                    FinancialOrderVo financialOrderVo = financialOrderItemVoMap.get(refund.getOrderItemId());
                    if(financialOrderVo != null) {
                        //本期订单退款,更新订单项财务VO中的退款信息,重新计算结算金额和货款
                        updateFinancialOrderVoByOrderRefund(financialOrderVo, refund);

                    } else {
                        //往期订单退款,为订单退款创建财务VO
                        if(hasPermissionToTheRepository(refund.getOrderItem().getOrder().getRepo())) {
                            financialOrderVo = createRefundFinancialOrderVoByOrderItemRefund(refund, refund.getOrderItem(), printedOrderApproveMap, invoicedOrderApproveMap);
                            financialOrderVoList.add(financialOrderVo);
                        }
                    }
                    break;
                }
                case PAYMENT: {
                    List<FinancialOrderVo> financialPaymentOrAllocationVoList = financialPaymentOrAllocationVoMap.get(refund.getPaymentId());
                    if(financialPaymentOrAllocationVoList != null) {
                        //本期预收款退款,更新预收款财务VO中的退款信息,重新计算结算金额和货款
                        updateFinancialOrderVoByPaymentRefund(financialPaymentOrAllocationVoList);

                    } else {
                        //往期预收款退款,为预收款退款创建财务VO
                        financialPaymentOrAllocationVoList = createRefundFinancialOrderVoByPaymentRefund(refund, refund.getPayment());
                        financialOrderVoList.addAll(financialPaymentOrAllocationVoList);

                    }
                    break;
                }
            }
        }

        return financialOrderVoList;



    }

    /**
     * 为预收款创建VO
     * @param financialOrderVoList
     * @param payment
     * @return
     */
    private List<FinancialOrderVo> createByPayment(List<FinancialOrderVo> financialOrderVoList, Payment payment) {
        List<PaymentAllocation> paymentAllocationList = paymentService.findPAByPaymentId(payment.getId());
        List<FinancialOrderVo> financialPaymentOrAllocationVoList = new ArrayList<FinancialOrderVo>();
        if(paymentAllocationList.isEmpty()) {
            //未分配
            FinancialOrderVo financialOrderVo = createByPayment(payment);
            if(financialOrderVoList != null) {
                financialOrderVoList.add(financialOrderVo);
            }
            financialPaymentOrAllocationVoList.add(financialOrderVo);

        } else {
            //已分配
            for(PaymentAllocation paymentAllocation : paymentAllocationList) {
                if(paymentAllocation.getPaymentFee().getAmount() > 0) {
                    //登录角色是仓库管理员,并且不是该仓库负责人的时候,返回为null
                    FinancialOrderVo financialOrderVo = createByPaymentAllocation(paymentAllocation, payment);
                    if(financialOrderVo != null) {
                        if(financialOrderVoList != null) {
                            financialOrderVoList.add(financialOrderVo);
                        }
                    }
                    financialPaymentOrAllocationVoList.add(financialOrderVo);
                }
            }
        }
        return financialPaymentOrAllocationVoList;
    }

    private List<FinancialOrderVo> createRefundFinancialOrderVoByPaymentRefund(Refund refund, Payment payment) {

        List<FinancialOrderVo> financialOrderVoList = createByPayment(null, payment);

        updateFinancialOrderVoByPaymentRefund(financialOrderVoList);

        for(FinancialOrderVo financialOrderVo : financialOrderVoList) {
            if(financialOrderVo == null) continue;
            FinancialOrderItemVo orderItemVo = financialOrderVo.getOrderItemVo();
            //因为是上期退款,所以设置单价为0
            orderItemVo.setPrice(Money.valueOf(0d));
            orderItemVo.setDiscountPrice(Money.valueOf(0d));
            orderItemVo.setDiscountFee(Money.valueOf(0d));
            orderItemVo.setSharedDiscountFee(Money.valueOf(0d));
            orderItemVo.setSelfSharedDiscountFee(Money.valueOf(0d));

            switch (payment.getType()) {
                case POST_COVER:
                    financialOrderVo.setFinancialOrderType(FinancialOrderType.POST_COVER_REFUND);
                    break;
                case SERVICE_COVER:
                    financialOrderVo.setFinancialOrderType(FinancialOrderType.SERVICE_COVER_REFUND);
                    break;
            }

            if(StringUtils.isBlank(financialOrderVo.getRepoName())) {
                calculatePaymentRefundFee(orderItemVo);

            } else {
                calculatePaymentAllocationRefundFee(payment, orderItemVo);

            }

            financialOrderVo.setPlatformRefundNo(refund.getPlatformRefundNo());

        }

        return financialOrderVoList;
    }

    private void updateFinancialOrderVoByPaymentRefund(List<FinancialOrderVo> financialPaymentOrAllocationVoList) {
        for(FinancialOrderVo financialOrderVo : financialPaymentOrAllocationVoList) {
            if(financialOrderVo == null) continue;
            //下面的代码假设一笔预收款只会有一次退款
            FinancialOrderItemVo orderItemVo = financialOrderVo.getOrderItemVo();
            if(StringUtils.isBlank(financialOrderVo.getRepoName())) {
                //未分配,设置退款金额
                Payment payment = paymentService.get(financialOrderVo.getPaymentId());
                orderItemVo.setRefundFee(payment.getRefundFee());
                orderItemVo.setActualRefundFee(payment.getRefundFee());

            } else {
                //已分配,设置退款金额
                PaymentAllocation paymentAllocation = generalDAO.get(PaymentAllocation.class, financialOrderVo.getPaymentAllocationId());
                orderItemVo.setRefundFee(paymentAllocation.getRefundFee());
                orderItemVo.setActualRefundFee(paymentAllocation.getRefundFee());
            }
            orderFeeService.calculateOrderItemFee(orderItemVo);
        }
    }

    private FinancialOrderVo createRefundFinancialOrderVoByOrderItemRefund(Refund refund, OrderItem orderItem,
                                                                           Map<Integer, OrderApprove> printedOrderApproveMap, Map<Integer, OrderApprove> invoicedOrderApproveMap) {

        FinancialOrderVo financialOrderVo = createByOrderItem(orderItem.getOrder(), orderItem, printedOrderApproveMap, invoicedOrderApproveMap);
        FinancialOrderItemVo orderItemVo = financialOrderVo.getOrderItemVo();

        //因为是上期退款,所以设置单价为0
        orderItemVo.setPrice(Money.valueOf(0d));
        orderItemVo.setDiscountPrice(Money.valueOf(0d));
        orderItemVo.setDiscountFee(Money.valueOf(0d));
        orderItemVo.setSharedDiscountFee(Money.valueOf(0d));
        orderItemVo.setSelfSharedDiscountFee(Money.valueOf(0d));

        if (refund.isOnline()) {
            financialOrderVo.setFinancialOrderType(FinancialOrderType.ONLINE_REFUND);
            orderItemVo.setRefundFee(refund.getRefundFee());
            orderItemVo.setActualRefundFee(refund.getActualRefundFee());
            orderItemVo.setReturnPostFee(refund.getPostFee());
            orderItemVo.setReturnPostPayer(refund.getPostPayer());

        } else {
            financialOrderVo.setFinancialOrderType(FinancialOrderType.OFFLINE_REFUND);
            orderItemVo.setOfflineRefundFee(refund.getRefundFee());
            orderItemVo.setOfflineReturnPostFee(refund.getPostFee());
            orderItemVo.setOfflineReturnPostPayer(refund.getPostPayer());

        }

        financialOrderVo.setRefundTime(refund.getRefundTime());
        financialOrderVo.setPlatformRefundNo(refund.getPlatformRefundNo());
        orderFeeService.calculateOrderItemFee(orderItemVo);


        return financialOrderVo;
    }

    private void updateFinancialOrderVoByOrderRefund(FinancialOrderVo financialOrderVo, Refund refund) {

        FinancialOrderItemVo financialOrderItemVo = financialOrderVo.getOrderItemVo();
        if(refund.isOnline()) {
            financialOrderItemVo.setRefundFee(financialOrderItemVo.getRefundFee().add(refund.getRefundFee()));
            financialOrderItemVo.setActualRefundFee(financialOrderItemVo.getActualRefundFee().add(refund.getActualRefundFee()));
            if(refund.isAlsoReturn()){
                financialOrderItemVo.setReturnPostPayer(refund.getPostPayer());
                financialOrderItemVo.setReturnPostFee(refund.getPostFee());
            }
        } else {
            financialOrderItemVo.setOfflineRefundFee(refund.getRefundFee());
            if(refund.isAlsoReturn()){
                financialOrderItemVo.setOfflineReturnPostPayer(refund.getPostPayer());
                financialOrderItemVo.setOfflineReturnPostFee(refund.getPostFee());
            }

        }

        orderFeeService.calculateOrderItemFee(financialOrderItemVo);

    }

    /**
     * 根据订单项创建VO
     * @param order
     * @param orderItem
     * @param printedOrderApproveMap
     * @param invoicedOrderApproveMap
     * @return
     */
    private FinancialOrderVo createByOrderItem(Order order, OrderItem orderItem, Map<Integer, OrderApprove> printedOrderApproveMap,
                                              Map<Integer, OrderApprove> invoicedOrderApproveMap) {

        FinancialOrderVo financialOrderVo = new FinancialOrderVo();

        //首先设置与订单相关的字段
        switch (order.getType()) {
            case NORMAL:
                financialOrderVo.setFinancialOrderType(FinancialOrderType.NORMAL_ORDER);
                break;

            case EXCHANGE:
                financialOrderVo.setFinancialOrderType(FinancialOrderType.EXCHANGE_ORDER);
                break;

            case REPLENISHMENT:
                financialOrderVo.setFinancialOrderType(FinancialOrderType.REPLENISHMENT_ORDER);
                break;
        }

        financialOrderVo.setPlatformType(order.getPlatformType());
        financialOrderVo.setShopName(order.getShop().getNick());
        financialOrderVo.setPlatformOrderNo(order.getPlatformOrderNo());
        financialOrderVo.setOrderNo(order.getOrderNo());
        financialOrderVo.setBuyTime(order.getBuyTime());
        financialOrderVo.setPayTime(order.getPayTime());
        financialOrderVo.setStatus(order.getStatus());
        financialOrderVo.setRepoName(order.getRepo().getName());
        financialOrderVo.setSharedDiscountFee(order.getSharedDiscountFee());
        financialOrderVo.setSelfSharedDiscountFee(order.getSelfSharedDiscountFee());
        OrderApprove printedOrderApprove = printedOrderApproveMap.get(order.getId());
        if(printedOrderApprove != null) {
            financialOrderVo.setPrintTime(printedOrderApprove.getUpdateTime());  //打印时间
        }
        financialOrderVo.setBuyerId(order.getBuyerId());
        if (order.getInvoice() != null) {
            if (order.getInvoice().getReceiver().getReceiverName() != null) {
                financialOrderVo.setReceiverName(order.getInvoice().getReceiver().getReceiverName());
            }
            if (order.getInvoice().getReceiver().getReceiverPhone() != null) {
                financialOrderVo.setReceiverPhone(order.getInvoice().getReceiver().getReceiverPhone());
            }
            if (order.getInvoice().getReceiver().getReceiverMobile() != null) {
                financialOrderVo.setReceiverMobile(order.getInvoice().getReceiver().getReceiverMobile());
            }
            if (order.getInvoice().getReceiver().getReceiverAddress() != null) {
                financialOrderVo.setReceiverAddress(order.getInvoice().getReceiver().getReceiverAddress());
            }
            if (order.getInvoice().getShippingComp() != null) {
                financialOrderVo.setShippingComp(DeliveryType.valueOf(order.getInvoice().getShippingComp()).getValue());
            }
            if (order.getInvoice().getShippingNo() != null) {
                financialOrderVo.setShippingNo(order.getInvoice().getShippingNo());
            }
        }

//      再设置与订单项有关的字段
        FinancialOrderItemVo financialOrderItemVo = createFinancialOrderItemVoByOrderItem(orderItem);


        orderFeeService.calculateOrderItemFee(financialOrderItemVo);
        financialOrderVo.setOrderItemVo(financialOrderItemVo);


        return financialOrderVo;
    }

    /**
     * 拼接OrderItemVo
     *
     * @param orderItem   订单项
     */

    private FinancialOrderItemVo createFinancialOrderItemVoByOrderItem(OrderItem orderItem) {

        FinancialOrderItemVo orderItemVo = new FinancialOrderItemVo();

        orderItemVo.setPlatformType(orderItem.getPlatformType());
        orderItemVo.setPostFee(orderItem.getInvoicePostFee());
        orderItemVo.setOuterSku(orderItem.getOuterSku());
        orderItemVo.setSpecInfo(orderItem.getSpecInfo());
        orderItemVo.setId(orderItem.getId());
        orderItemVo.setPlatformSubOrderNo(orderItem.getPlatformSubOrderNo());
        orderItemVo.setType(orderItem.getType().getValue());
        orderItemVo.setStatus(orderItem.getStatus().toString());
        orderItemVo.setReturnStatus(orderItem.getReturnStatus());
        orderItemVo.setOfflineReturnStatus(orderItem.getOfflineReturnStatus());
        orderItemVo.setProductCode(orderItem.getProductCode());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductSku(orderItem.getProductSku());
        orderItemVo.setExchangedGoods(OrderUtil.getExchangeGoodsInfo(orderItem, generalDAO));
        Product product = orderItem.getProduct();
        if (product != null) {
            Brand brand = product.getBrand();
            orderItemVo.setColor(orderItem.getProduct().getColor());
            ProductCategory category = product.getCategory();
            orderItemVo.setBrandName(brand != null ? brand.getName() : null);
            orderItemVo.setCateName(category != null ? category.getName() : null);
        }
        orderItemVo.setPrice(orderItem.getPrice());
        orderItemVo.setBuyCount(orderItem.getBuyCount());
        orderItemVo.setRealBuyCount(orderItem.getRealBuyCount());
        orderItemVo.setDiscountPrice(orderItem.getDiscountPrice());
        orderItemVo.setDiscountFee(orderItem.getDiscountFee());
        orderItemVo.setSharedDiscountFee(orderItem.getSharedDiscountFee());
        orderItemVo.setSelfSharedDiscountFee(orderItem.getSelfSharedDiscountFee());
        orderItemVo.setSharedPostFee(orderItem.getSharedPostFee());
//        orderItemVo.setPostCoverFee(orderItem.getPostCoverFee());
//        orderItemVo.setPostCoverRefundFee(orderItem.getPostCoverRefundFee());
//        orderItemVo.setServiceCoverFee(orderItem.getServiceCoverFee());
//        orderItemVo.setServiceCoverRefundFee(orderItem.getServiceCoverRefundFee());
//        orderItemVo.setRefundFee(orderItem.getRefundFee());
//        orderItemVo.setActualRefundFee(orderItem.getActualRefundFee());
//        orderItemVo.setOfflineRefundFee(orderItem.getOfflineRefundFee());
        orderItemVo.setReturnPostFee(orderItem.getReturnPostFee());
        orderItemVo.setReturnPostPayer(orderItem.getReturnPostPayer());
        orderItemVo.setOfflineReturnPostFee(orderItem.getOfflineReturnPostFee());
        orderItemVo.setOfflineReturnPostPayer(orderItem.getOfflineReturnPostPayer());
        orderItemVo.setExchangePostFee(orderItem.getExchangePostFee());
        orderItemVo.setExchangePostPayer(orderItem.getExchangePostPayer());
        if (orderItem.getExchangeSourceId() != null) {
            orderItemVo.setExchangeSourceId(orderItem.getExchangeSourceId());
        }

        orderItemVo.setType(orderItem.getType().getValue());  //订单项类型
        orderItemVo.setStatus(orderItem.getStatus().getValue());//订单项状态

        return orderItemVo;
    }

    /**
     * 为未分配预收款创建VO
     * @param payment
     * @return
     */
    private FinancialOrderVo createByPayment(Payment payment) {

        FinancialOrderVo financialOrderVo = new FinancialOrderVo();

        //首先设置与订单相关的字段
        switch (payment.getType()) {
            case SERVICE_COVER:
                financialOrderVo.setFinancialOrderType(FinancialOrderType.SERVICE_COVER);
                break;

            case POST_COVER:
                financialOrderVo.setFinancialOrderType(FinancialOrderType.POST_COVER);
                break;

            default:
                throw new StewardBusinessException("创建预收款VO的时候发现不能处理的预收款类型:" + payment.getType().toString());
        }

        financialOrderVo.setPlatformType(payment.getPlatformType());
        financialOrderVo.setShopName(payment.getShop().getNick());
        financialOrderVo.setPlatformOrderNo(payment.getPlatformOrderNo());
        financialOrderVo.setBuyTime(payment.getBuyTime());
        financialOrderVo.setPayTime(payment.getPayTime());
        financialOrderVo.setBuyerId(payment.getBuyerId());
        financialOrderVo.setPaymentId(payment.getId());

        FinancialOrderItemVo financialOrderItemVo = new FinancialOrderItemVo();
        financialOrderItemVo.setPrice(Money.valueOf(1d));
        financialOrderItemVo.setBuyCount(payment.getOriginalOrderItem().getBuyCount().intValue());
        financialOrderItemVo.setRealBuyCount(financialOrderItemVo.getBuyCount());

        //计算金额
        calculatePaymentRefundFee(financialOrderItemVo);
        financialOrderVo.setOrderItemVo(financialOrderItemVo);

        return financialOrderVo;
    }

    /**
     * 为预收款分配记录创建VO
     * 登录角色是仓库管理员,并且不是该仓库负责人的时候,返回为null
     * @param paymentAllocation
     * @param payment
     * @return
     */
    private FinancialOrderVo createByPaymentAllocation(PaymentAllocation paymentAllocation, Payment payment) {

        OrderItem orderItem = generalDAO.get(OrderItem.class, paymentAllocation.getOrderItemId());
        Repository repository = orderItem.getOrder().getRepo();
        if(!hasPermissionToTheRepository(repository)) {
            //判断能否看见这个仓库的数据
            return null;
        }

        FinancialOrderVo financialOrderVo = createByPayment(payment);
        financialOrderVo.setRepoName(repository.getName());
        financialOrderVo.setPaymentAllocationId(paymentAllocation.getId());
        financialOrderVo.setPaymentAllocationPlatformOrderNo(orderItem.getPlatformOrderNo());
        financialOrderVo.setPaymentAllocationOrderNo(orderItem.getOrder().getOrderNo());

        FinancialOrderItemVo financialOrderItemVo = financialOrderVo.getOrderItemVo();
        //补差的分配金额就等于订购数量
        financialOrderItemVo.setBuyCount(paymentAllocation.getPaymentFee().getAmountWithBigDecimal().intValue());
        financialOrderItemVo.setRealBuyCount(financialOrderItemVo.getBuyCount());

        //计算金额
        calculatePaymentAllocationRefundFee(payment, financialOrderItemVo);

        return financialOrderVo;
    }

    private boolean hasPermissionToTheRepository(Repository repository) {
        Employee employee = SessionUtils.getEmployee();
        if(employee != null && employee.isRepositoryEmployee()) {
            //判断能否看见这个仓库的数据
            if(!repositoryService.isRepositoryCharger(employee.getId(), repository.getId())) {
                //不是该仓库负责人
                return false;
            }
        }
        return true;
    }

    private void calculatePaymentRefundFee(FinancialOrderItemVo financialOrderItemVo) {
        orderFeeService.calculateOrderItemFee(financialOrderItemVo);
        financialOrderItemVo.setGoodsFee(Money.valueOf(0d));
    }

    private void calculatePaymentAllocationRefundFee(Payment payment, FinancialOrderItemVo financialOrderItemVo) {
        orderFeeService.calculateOrderItemFee(financialOrderItemVo);
        if(!payment.getType().equals(PaymentType.SERVICE_COVER)) {
            //不是服务补差的预收款不计入货款
            financialOrderItemVo.setGoodsFee(Money.valueOf(0d));
        }
    }

}
