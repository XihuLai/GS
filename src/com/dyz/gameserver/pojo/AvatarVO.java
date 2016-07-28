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
     * 当前牌组，踢出掉了每次的吃，碰，杠，胡
     */
   // public int [][] currentCardList;
    /**
     * 牌数组
     * /碰 1  杠2  胡3  吃4
     */
    private int[][] paiArray;
/*    *//**
     * 有一个规则（很重要：在一圈内（这里的一圈标识一人抓了一次牌）如若A玩家听牌胡 二 五条 ，
     * B玩家打出 二五条此时A玩家没有选择胡牌，那么在这一圈内，C D玩家如果也打得出二五条，
     * A玩家不能胡牌，直到A玩家下一次摸牌后（自摸可以）， 此时有人打了就可以胡了，如若又没胡，
     * 那么在这一圈内 其他玩家打的也不能胡）
     *//*
    private boolean canHu = true;
*/    
    /**
     * 存储整局牌的 杠，胡以及得分情况的对象，游戏结束时直接返回对象
     */
    private HuReturnObjectVO  huReturnObjectVO;
    
    

//	public int[][] getCurrentCardList() {
//		return currentCardList;
//	}
	
	/*public void setCurrentCardList(int[][] currentCardList) {
		this.currentCardList =getPaiArray().clone();
	}*/

	/**
	 * 从牌的数组中踢出传入的牌的index
	 * @param cardIndex
	 */
	/*public void updateCurrentCardList(int ...cardIndex) {
		for (int i = 0; i < cardIndex.length; i++) {
			this.currentCardList[0][cardIndex[i]] = currentCardList[0][cardIndex[i]]-1;
		}
	}*/

    
   public HuReturnObjectVO getHuReturnObjectVO() {
		return huReturnObjectVO;
	}

	public void setHuReturnObjectVO(HuReturnObjectVO huReturnObjectVO) {
		this.huReturnObjectVO = huReturnObjectVO;
	}
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
