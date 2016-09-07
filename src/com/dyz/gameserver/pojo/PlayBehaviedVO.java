package com.dyz.gameserver.pojo;

import java.util.List;

/**
 * 打牌操作
 * @author luck
 *
 */
public class PlayBehaviedVO {
	//public int id;//	int(11)	主键	
	public int type;//	         char(1)	1出牌，2摸牌，3吃，4碰，5杠，6胡(自摸/点炮),7抢胡,8抓码.....
	public String cardIndex;//	varchar(11)	进行type操作时的牌(当为吃的时候存cardIndex1,cardIndex2,cardIndex3)
	public int accountindex_id;//	int(11)	索引
	public int recordindex;//	int(11)	记录序号
	//public long currentTime;//	datestamp	当前操作时间，存时间戳（long）	
	//public int status;//	char(1)	0: 正常  1:删除/注销 	
	public int gangType;//杠的类型，1-别人点杠，2-自己暗杠，3-自己摸起来杠   4:-抢杠
	public String ma;//抓的码   格式(1:2:3:5:6:8)
	public List<Integer> valideMa;//有效码
	
	
	/*public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}*/
	
	public List<Integer> getValideMa() {
		return valideMa;
	}
	public void setValideMa(List<Integer> valideMa) {
		this.valideMa = valideMa;
	}
	public int getType() {
		return type;
	}
	public String getMa() {
		return ma;
	}
	public void setMa(String ma) {
		this.ma = ma;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCardIndex() {
		return cardIndex;
	}
	public void setCardIndex(String cardIndex) {
		this.cardIndex = cardIndex;
	}
	public int getAccountindex_id() {
		return accountindex_id;
	}
	public void setAccountindex_id(int accountindex_id) {
		this.accountindex_id = accountindex_id;
	}
	public int getRecordindex() {
		return recordindex;
	}
	public void setRecordindex(int recordindex) {
		this.recordindex = recordindex;
	}
/*	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}*/
//	public int getStatus() {
//		return status;
//	}
//	public void setStatus(int status) {
//		this.status = status;
//	}
	public int getGangType() {
		return gangType;
	}
	public void setGangType(int gangType) {
		this.gangType = gangType;
	}

	
	
}
