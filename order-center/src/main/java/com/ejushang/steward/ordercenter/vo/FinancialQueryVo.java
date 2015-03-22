package com.ejushang.steward.ordercenter.vo;

/**
 * User: liubin
 * Date: 14-8-21
 */
public class FinancialQueryVo {

    //buyTime,payTime
    private String searchTimeType;

    private String startTime;

    private String endTime;

    private String platformType;

    private Integer shopId;

    private Integer repoId;

    public String getSearchTimeType() {
        return searchTimeType;
    }

    public void setSearchTimeType(String searchTimeType) {
        this.searchTimeType = searchTimeType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

}
