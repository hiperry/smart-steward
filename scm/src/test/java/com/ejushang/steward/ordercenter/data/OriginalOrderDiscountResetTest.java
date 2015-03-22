package com.ejushang.steward.ordercenter.data;

import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.service.OriginalOrderService;
import com.ejushang.steward.ordercenter.service.UpdateDataService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

/**
 * User: Baron.Zhang
 * Date: 2014/8/22
 * Time: 14:20
 */
public class OriginalOrderDiscountResetTest extends BaseTest{

    @Autowired
    private UpdateDataService updateDataService;

    @Test
    @Transactional
    @Rollback(false)
    public void resetDiscountInfo() throws Exception{

//        updateDataService.deleteOrder(Lists.newArrayList(34416, 34758, 35234, 37763));

//        updateDataService.checkIncorrectOrderItemAndOriginalOrderItem();

        updateDataService.resetDiscountInfo();

    }



}
