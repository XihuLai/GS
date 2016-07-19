package com.dyz.gameserver.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

/**
 * Created by kevin on 2016/6/22.
 */
public class RoomVO {
    /**
     * 房间ID
     */
    private int roomId;
    /**
     * 房间的使用次数
     */
    private int roundNumber;
    /**
     *是否红中当赖子
     */
    private boolean hong;
    /**
     * 房间模式，1-转转麻将。2-划水麻将。3-长沙麻将
     */
    private int roomType;
    /**
     *七小对
     */
    private boolean sevenDouble;
    /**
     *抓码的个数
     */
    private int ma;
    /**
     *是否自摸胡，还是可以抢杠胡
     */
    private int ziMo;
    /**
     * 下鱼
     */
    private int xiaYu;
    /**
     * 是否要字牌
     */
    private boolean addWordCard;


    public String name;
    
    private List<AvatarVO> playerList;

    /**
     * 开一个房间几局游戏完后，统计所有玩家的杠，胡次数
     *type: 1:自摸次数
     * 2:接炮次数
     * 3:点炮次数
     * 4：明杠次数
     * 5：暗杠次数
     * 6: 总成绩
     * 
     */
    public Map<Integer , Integer> map;
    
    public Map<Integer, Integer> getMap() {
		return map;
	}

	public synchronized void updateMap(int type ) {
		if(map == null){
			map = new HashMap<Integer , Integer>();
		}
		if(map.get(type) == null){
			map.put(type, 1);
		}
		else{
			map.put(type, 1+map.get(type));
		}
	}

	public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public int getZiMo() {
        return ziMo;
    }

    public void setZiMo(int ziMo) {
        this.ziMo = ziMo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHong() {
        return hong;
    }

    public void setHong(boolean hong) {
        this.hong = hong;
    }

    public boolean getSevenDouble() {
        return sevenDouble;
    }

    public void setSevenDouble(boolean sevenDouble) {
        this.sevenDouble = sevenDouble;
    }

    public int getXiaYu() {
        return xiaYu;
    }

    public void setXiaYu(int xiaYu) {
        this.xiaYu = xiaYu;
    }

    public List<AvatarVO> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<AvatarVO> playerList) {
        this.playerList = playerList;
    }

    public boolean isAddWordCard() {
        return addWordCard;
    }

    public void setAddWordCard(boolean addWordCard) {
        this.addWordCard = addWordCard;
    }
}
