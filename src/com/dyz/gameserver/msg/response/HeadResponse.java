package com.dyz.gameserver.msg.response;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by kevin on 2016/7/26.
 */
public class HeadResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param message 
     */
    public HeadResponse(int status, String message) {
        super(status, ConnectAPI.headRESPONSE);
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
