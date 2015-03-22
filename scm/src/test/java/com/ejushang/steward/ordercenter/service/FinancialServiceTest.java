package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.Application;
import com.ejushang.steward.common.domain.BusinessLog;
import com.ejushang.steward.common.domain.SqlLog;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.page.Page;
import com.ejushang.steward.common.util.JsonResult;
import com.ejushang.steward.common.util.JsonUtil;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.ErrorInfo;
import com.ejushang.steward.ordercenter.domain.Order;
import com.ejushang.steward.ordercenter.domain.Product;
import com.ejushang.steward.ordercenter.vo.FinancialQueryVo;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * User: liubin
 * Date: 14-3-10
 */
public class FinancialServiceTest extends BaseTest {

    @Autowired
    private FinancialService financialService;

    @Test
    @Transactional(readOnly = true)
    public void test() throws Exception {

        exportExcel("financial_report_tmall.xls", PlatformType.TAO_BAO);
        exportExcel("financial_report_jd.xls", PlatformType.JING_DONG);

    }

    private void exportExcel(String fileName, PlatformType platformType) throws Exception {
        String tempPath = System.getProperty("java.io.tmpdir");
        String filePath = tempPath + File.separator + fileName;
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }

        FinancialQueryVo financialQueryVo = new FinancialQueryVo();
        financialQueryVo.setSearchTimeType("buyTime");
        financialQueryVo.setStartTime("2014-08-01 00:00:00");
        financialQueryVo.setEndTime("2014-08-09 23:59:59");
        financialQueryVo.setPlatformType(platformType.toString());
//        financialQueryVo.setShopId();
        Workbook workbook = financialService.reportOrderItem(financialQueryVo);
        workbook.write(new FileOutputStream(file));

        System.out.println(String.format("文件[%s]已经成功生成", filePath));
    }


}
