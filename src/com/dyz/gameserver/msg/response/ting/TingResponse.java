package com.dyz.gameserver.msg.response.ting;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

/**
 * Created by Westlake on 17/1/6.
 */

public class TingResponse  extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param msgCode
     */
    public TingResponse(int status, boolean bset, int idx) {
        super(status, ConnectAPI.TINGPAI_RESPONSE);
        JSONObject json = new JSONObject();
        json.put("ting_settled", bset);
        json.put("avatarIndex", idx);
        if (status > 0) {
            try {
                output.writeUTF(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                output.close();
            }
        }
//        entireMsg();
    }
}
