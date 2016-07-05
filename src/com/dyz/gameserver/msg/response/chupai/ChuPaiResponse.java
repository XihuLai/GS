package com.dyz.gameserver.msg.response.chupai;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.Card;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/23.
 */
public class ChuPaiResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     */
    public ChuPaiResponse(int status, String str) {
        super(status, ConnectAPI.CHUPAI_RESPONSE);
        if(status >0){
            try {
                output.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entireMsg();
    }
}
