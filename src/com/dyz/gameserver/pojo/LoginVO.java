package com.dyz.gameserver.pojo;

/**
 * Created by kevin on 2016/6/23.
 */
public class LoginVO {
    private String openId;

    private String nickName;

    private String headIcon;

    private String unionid;

    private String province;

    private String city;

    private int sex;
    
    private String IP;


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

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}
    
}
