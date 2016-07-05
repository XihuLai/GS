package com.dyz.gameserver.pojo;

/**
 * Created by kevin on 2016/6/18.
 */
public class Card extends Object {
    /**
     * 牌面点数
     * 0-8是万字
     * 9-17是条子
     * 18-27是筒子
     * 28-34是字牌
     * 如果是字的话 1-东，2-南，3-西，4-北，5-中，6-发，7-白
     */
    public int point;

    @Override
    public String toString() {
        return "Card{" +
                ", point=" + point +
                '}';
    }

    public Card(int point){
        this.point = point;
    }

}
