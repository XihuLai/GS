package com.dyz.gameserver.msg.response.startgame;

import java.io.IOException;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

public class StartGameResponse extends ServerResponse {
    /**
    *
    * @param status
    * @param paiArray 自己的牌数组
    * @param bankerId 庄家ID
    */
   public StartGameResponse(int status, int[][] paiArray,int bankerId,int dice1,int dice2,Map<String,Integer> indexMap) {
       super(status, ConnectAPI.STARTGAME_RESPONSE);
       try {
           JSONObject json = new JSONObject();
           json.put("paiArray",paiArray);
           json.put("bankerId",bankerId);
           json.put("dice1",dice1);
           json.put("dice2",dice2);
           json.put("avatarIndexMap", indexMap);
           output.writeUTF(json.toString());
       } catch (IOException e) {
           e.printStackTrace();
       }
       entireMsg();
   }

}
