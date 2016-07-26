package com.dyz.myBatis.model;

import java.util.Date;

public class GameRecord {
    private Integer id;

    private Integer gameId;

    private String type;

    private String cardindex;

    private Integer acountindexId;

    private Date curenttime;

    private String playerlistOne;

    private String playerlistTwo;

    private String playerlistThree;

    private String playerlistFour;

    private String status;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getCardindex() {
        return cardindex;
    }

    public void setCardindex(String cardindex) {
        this.cardindex = cardindex == null ? null : cardindex.trim();
    }

    public Integer getAcountindexId() {
        return acountindexId;
    }

    public void setAcountindexId(Integer acountindexId) {
        this.acountindexId = acountindexId;
    }

    public Date getCurenttime() {
        return curenttime;
    }

    public void setCurenttime(Date curenttime) {
        this.curenttime = curenttime;
    }

    public String getPlayerlistOne() {
        return playerlistOne;
    }

    public void setPlayerlistOne(String playerlistOne) {
        this.playerlistOne = playerlistOne == null ? null : playerlistOne.trim();
    }

    public String getPlayerlistTwo() {
        return playerlistTwo;
    }

    public void setPlayerlistTwo(String playerlistTwo) {
        this.playerlistTwo = playerlistTwo == null ? null : playerlistTwo.trim();
    }

    public String getPlayerlistThree() {
        return playerlistThree;
    }

    public void setPlayerlistThree(String playerlistThree) {
        this.playerlistThree = playerlistThree == null ? null : playerlistThree.trim();
    }

    public String getPlayerlistFour() {
        return playerlistFour;
    }

    public void setPlayerlistFour(String playerlistFour) {
        this.playerlistFour = playerlistFour == null ? null : playerlistFour.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}