package com.dyz.myBatis.model;

public class StandingsAccountRelation {
    private Integer id;

    private Integer standingsId;

    private Integer accountId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStandingsId() {
        return standingsId;
    }

    public void setStandingsId(Integer standingsId) {
        this.standingsId = standingsId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}