package com.dyz.gameserver.msg.response.messageBox;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by kevin on 2016/8/2.
 */
public class MessageBoxResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     *
     * @param msgCode
     */
    public MessageBoxResponse(String msgCode) {
        super(1, ConnectAPI.MessageBox_Notice);
        try {
            output.writeUTF(msgCode);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            output.close();
        }
    }
}
