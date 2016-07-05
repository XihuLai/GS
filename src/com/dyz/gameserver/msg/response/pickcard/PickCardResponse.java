package com.dyz.gameserver.msg.response.pickcard;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/23.
 */
public class PickCardResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     * msgCode == 100 时表示是询问海底捞还是不捞
     */
    public PickCardResponse(int status, int msgCode) {
        super(status, ConnectAPI.PICKCARD_REQUEST);
        if(status >0){
            try {
                output.writeInt(msgCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entireMsg();
    }
}
