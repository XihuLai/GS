package com.dyz.gameserver.pojo;


/**
 * 记录打牌玩家信息
 * @author luck
 *
 */
public class PlayRecordItemVO {
	//public int id;
	public String accountName;//		玩家名
	public int accountIndex;//	int(1)	索引(游戏时对应的索引)	
	public String cardList;//	初始牌组成的字符串
	public String headIcon;//头像
	public int socre;//分数
	public int sex;//性别
	public int gameRound;//当前局数
	public int uuid;//用户uuid
	//public int gold;//当前金币
	/*public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}*/
	
	public String getAccountName() {
		return accountName;
	}
	public int getUuid() {
		return uuid;
	}
	public void setUuid(int uuid) {
		this.uuid = uuid;
	}

	public int getGameRound() {
		return gameRound;
	}
	public void setGameRound(int gameRound) {
		this.gameRound = gameRound;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public int getAccountIndex() {
		return accountIndex;
	}
	public void setAccountIndex(int accountIndex) {
		this.accountIndex = accountIndex;
	}
	public String getCardList() {
		return cardList;
	}
	public void setCardList(String cardList) {
		this.cardList = cardList;
	}
	public String getHeadIcon() {
		return headIcon;
	}
	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}
	public int getSocre() {
		return socre;
	}
	public void setSocre(int socre) {
		this.socre = socre;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
/*	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}*/

	
	
}
