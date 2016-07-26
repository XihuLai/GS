package com.dyz.gameserver.msg.response.hu;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * 一个房间次数用完之后返回全局信息
 * @author luck
 *
 */
public class HuPaiAllResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param  str
     */
    public HuPaiAllResponse(int status, String str) {
        super(status, ConnectAPI.HUPAIALL_RESPONSE);
        if(status >0){
            try {
            	//格式
                output.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
        //entireMsg();
    }
}
