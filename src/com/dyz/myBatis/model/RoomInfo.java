package com.dyz.myBatis.model;

public class RoomInfo {
    private Integer id;

    private String gametype;

    private String ishong;

    private Integer roomid;

    private String sevendouble;

    private Integer ma;

    private String zimo;

    private Integer xiayu;

    private String addwordcard;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype == null ? null : gametype.trim();
    }

    public String getIshong() {
        return ishong;
    }

    public void setIshong(String ishong) {
        this.ishong = ishong == null ? null : ishong.trim();
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public String getSevendouble() {
        return sevendouble;
    }

    public void setSevendouble(String sevendouble) {
        this.sevendouble = sevendouble == null ? null : sevendouble.trim();
    }

    public Integer getMa() {
        return ma;
    }

    public void setMa(Integer ma) {
        this.ma = ma;
    }

    public String getZimo() {
        return zimo;
    }

    public void setZimo(String zimo) {
        this.zimo = zimo == null ? null : zimo.trim();
    }

    public Integer getXiayu() {
        return xiayu;
    }

    public void setXiayu(Integer xiayu) {
        this.xiayu = xiayu;
    }

    public String getAddwordcard() {
        return addwordcard;
    }

    public void setAddwordcard(String addwordcard) {
        this.addwordcard = addwordcard == null ? null : addwordcard.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}