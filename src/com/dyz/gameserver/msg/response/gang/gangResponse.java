package com.dyz.gameserver.msg.response.gang;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * 
 * @author luck
 *
 */
public class gangResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     */
    public gangResponse(int status, String str) {
        super(status, ConnectAPI.GANGPAI_RESPONSE);
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
