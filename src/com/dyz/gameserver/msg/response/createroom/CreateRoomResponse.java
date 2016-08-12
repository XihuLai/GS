package com.dyz.gameserver.msg.response.createroom;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * Created by kevin on 2016/6/22.
 */
public class CreateRoomResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param
     */
    public CreateRoomResponse(int status,String obj) throws IOException {
        super(status,ConnectAPI.CREATEROOM_RESPONSE);
        if(status > 0){
        	output.writeUTF(obj);
        	System.out.println("roomId:"+obj);
        	output.close();
        	
        }
    }
}
