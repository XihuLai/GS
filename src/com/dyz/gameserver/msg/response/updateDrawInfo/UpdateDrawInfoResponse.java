package com.dyz.gameserver.msg.response.updateDrawInfo;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * 每天重置抽奖信息之后通知在线玩家，
 * @author luck
 *
 */
public class UpdateDrawInfoResponse extends ServerResponse {
	
	
    public UpdateDrawInfoResponse(int status, String content) {
        super(status, ConnectAPI.HOST_UPDATEDRAW_RESPONSE);
        if(status >0){
            try {
                output.writeUTF(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
       // entireMsg();
    }
}
