package com.dyz.gameserver.pojo;

import com.dyz.myBatis.model.Account;

/**
 * Created by kevin on 2016/6/23.
 */
public class AvatarVO {
    /**
     * 用户基本信息
     */
    private Account account;
    /**
     * 房间号
     */
    private int roomId;
    /**
     * 是否准备
     */
    private boolean isReady = false;
    /**
     * 是否是庄家
     */
    private boolean isMain = false;
    /**
     * 是否在线
     */
    private boolean isOnLine = false;
    /**
     * 牌数组
     */
    private int[][] paiArray;
    
    
    public Account getAccount() {
        return account;
    }

	public void setAccount(Account account) {
        this.account = account;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(boolean ready) {
        isReady = ready;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public boolean getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public int[][] getPaiArray() {
        return paiArray;
    }

    public void setPaiArray(int[][] paiArray) {
        this.paiArray = paiArray;
    }
}
