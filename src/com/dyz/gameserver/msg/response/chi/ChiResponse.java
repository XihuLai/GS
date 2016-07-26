package com.dyz.gameserver.msg.response.chi;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * 
 * @author luck
 *
 */
public class ChiResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     */
    public ChiResponse(int status, String str) {
        super(status, ConnectAPI.CHIPAI_RESPONSE);
        if(status >0){
            try {
                output.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
            	 output.close();
			}
        }
        //entireMsg();
    }
}
