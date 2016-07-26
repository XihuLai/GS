package com.dyz.gameserver.msg.response;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/22.
 */
public class ErrorResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param message
     */
    public ErrorResponse(String message) throws IOException {
        super(1,ConnectAPI.ERROR_RESPONSE);
        output.writeUTF(message);
       	output.close();
       // entireMsg();
    }
}
