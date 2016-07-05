package com.dyz.gameserver.pojo;

/**
 * Created by kevin on 2016/6/23.
 */
public class LoginVO {
    private String openId;

    private String nickName;

    private String headIcon;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }
}
