package com.dyz.gameserver.pojo;

/**
 * Created by kevin on 2016/9/12.
 */
public class CheckObjectVO {
    public int[] paiArray;
    public int isJiang;
    public String ToString(){
        String result = "";
        if(paiArray != null){
            for(int i=0;i<paiArray.length;i++){
                result += paiArray[i];
            }
        }
        return  result;
    }
}
