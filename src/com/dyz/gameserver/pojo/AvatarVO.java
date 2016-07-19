package com.dyz.gameserver.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     * 
     */
    private int[][] paiArray;
    /**
     * 有一个规则（很重要：在一圈内（这里的一圈标识一人抓了一次牌）如若A玩家听牌胡 二 五条 ，
     * B玩家打出 二五条此时A玩家没有选择胡牌，那么在这一圈内，C D玩家如果也打得出二五条，
     * A玩家不能胡牌，直到A玩家下一次摸牌后（自摸可以）， 此时有人打了就可以胡了，如若又没胡，
     * 那么在这一圈内 其他玩家打的也不能胡）
     */
    private boolean canHu = true;
    /**
     * key:type:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
     * value:list里面，第一个为点炮/杠/胡次数，第二个元素为点炮/杠/胡分数总和
     */
    private Map<Integer , ArrayList<Integer>> scoreRecord;
    
    
    public Map<Integer, ArrayList<Integer>> getScoreRecord() {
		return scoreRecord;
	}
    /**
     *  游戏碰1，杠2，吃3，胡4记录(key),
     * @param type 类型 
     * @param score 分数
     */
	public synchronized void updateScoreRecord(int type , int score) {
		if(scoreRecord == null){
			scoreRecord = new HashMap<Integer, ArrayList<Integer>>();
		}
		ArrayList<Integer> list = scoreRecord.get(type);
		if(list == null){
			list = new ArrayList<Integer>();
			list.add(1);
			list.add(score);
		}
		else{
			//在原来的基础上修改信息
			list = scoreRecord.get(type);
			list.add(list.get(0)+1);
			list.add(list.get(1)+score);
		}
		scoreRecord.put(type, list);
	}

	public boolean isCanHu() {
		return canHu;
	}

	public void setCanHu(boolean canHu) {
		this.canHu = canHu;
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
