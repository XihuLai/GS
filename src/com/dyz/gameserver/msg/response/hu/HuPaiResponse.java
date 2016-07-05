package com.dyz.gameserver.msg.response.hu;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * 
 * @author luck
 *
 */
public class HuPaiResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     */
    public HuPaiResponse(int status, String str) {
        super(status, ConnectAPI.HUPAI_RESPONSE);
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
