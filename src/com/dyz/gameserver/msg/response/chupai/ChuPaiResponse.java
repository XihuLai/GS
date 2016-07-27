package com.dyz.gameserver.msg.response.chupai;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/23.
 */
public class ChuPaiResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status  cardIndex牌点数   curAvatarIndex出牌人的索引
     * @param 
     */
    public ChuPaiResponse(int status, int cardIndex,int curAvatarIndex) {
        super(status, ConnectAPI.CHUPAI_RESPONSE);
        JSONObject json = new JSONObject();
        json.put("cardIndex", cardIndex);
        json.put("curAvatarIndex", curAvatarIndex);
        if(status >0){
            try {
                output.writeUTF(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
       // entireMsg();
    }
}
