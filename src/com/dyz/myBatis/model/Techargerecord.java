package com.dyz.myBatis.model;

import java.util.Date;

public class Techargerecord {
    private Integer id;

    private Integer accountId;

    private Integer managerId;

    private Integer managerUpId;

    private Date createtime;

    private Integer techargemoney;

    private String mark;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getManagerUpId() {
        return managerUpId;
    }

    public void setManagerUpId(Integer managerUpId) {
        this.managerUpId = managerUpId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getTechargemoney() {
        return techargemoney;
    }

    public void setTechargemoney(Integer techargemoney) {
        this.techargemoney = techargemoney;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark == null ? null : mark.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}