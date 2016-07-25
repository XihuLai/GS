package com.dyz.gameserver.msg.response.common;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 *  一个房间次数用完之后返回全局信息
 * @author luck
 *
 */
public class ReturnInfoResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param  str
     */
    public ReturnInfoResponse(int status, String str) {
        super(status, ConnectAPI.RETURN_INFO_RESPONSE);
        if(status >0){
            try {
            	//格式
                output.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entireMsg();
    }
}
