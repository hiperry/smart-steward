package com.ejushang.steward.ordercenter.data;

import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.service.UpdateDataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

/**
 * User: Baron.Zhang
 * Date: 2014/8/22
 * Time: 14:20
 */
public class UpdateOrderItemValidToFalseTest extends BaseTest{

    @Autowired
    private UpdateDataService updateDataService;

    @Test
    @Transactional
    @Rollback(false)
    public void updateOrderItemValidToFalse() throws Exception{

        updateDataService.updateOrderItemValidToFalse();

    }



}
