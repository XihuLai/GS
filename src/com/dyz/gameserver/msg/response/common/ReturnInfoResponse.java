package com.dyz.gameserver.msg.response.common;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 *  返回碰 胡 杠 吃的信息
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
        //entireMsg();
    }
}
