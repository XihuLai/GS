package com.dyz.gameserver.pojo;

/**
 * Created by kevin on 2016/7/5.
 */
public class CardVO {
    private int cardPoint;
    
    private int onePoint;//两个吃牌之一
    
    private int twoPoint;//两个吃牌之二
    
    private String type;//代表胡的类型（qiangHu）

    
    
  public int getOnePoint() {
		return onePoint;
	}

	public void setOnePoint(int onePoint) {
		this.onePoint = onePoint;
	}

	public int getTwoPoint() {
		return twoPoint;
	}

	public void setTwoPoint(int twoPoint) {
		this.twoPoint = twoPoint;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCardPoint() {
        return cardPoint;
    }

    public void setCardPoint(int cardPoint) {
        this.cardPoint = cardPoint;
    }
}
