package com.dyz.myBatis.model;

import java.util.Date;

public class Game {
    private Integer id;

    private Date createtime;

    private Date endttime;

    private Integer roomId;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getEndttime() {
        return endttime;
    }

    public void setEndttime(Date endttime) {
        this.endttime = endttime;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}