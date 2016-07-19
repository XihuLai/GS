package com.dyz.gameserver.pojo;

import java.util.List;

/**
 * Created by kevin on 2016/7/5.
 */
public class GangBackVO {
    private List<Integer> cardList;
    
    private int type;// 明杠 0    暗杠 1

    public List<Integer> getCardList() {
        return cardList;
    }

    public void setCardList(List<Integer> cardList) {
        this.cardList = cardList;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
    
    
}
