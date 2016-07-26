package com.dyz.myBatis.model;

public class GameAccountIndex {
    private Integer id;

    private Integer gameId;

    private Integer accountId;

    private Integer accountindex;

    private String cardlist;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getAccountindex() {
        return accountindex;
    }

    public void setAccountindex(Integer accountindex) {
        this.accountindex = accountindex;
    }

    public String getCardlist() {
        return cardlist;
    }

    public void setCardlist(String cardlist) {
        this.cardlist = cardlist == null ? null : cardlist.trim();
    }
}