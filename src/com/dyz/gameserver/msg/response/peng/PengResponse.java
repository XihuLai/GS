package com.dyz.gameserver.msg.response.peng;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * 
 * @author luck
 *
 */
public class PengResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param
     */
    public PengResponse(int status, int cardPoint,int AvatarId) {
        super(status, ConnectAPI.PENGPAI_RESPONSE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardPoint",cardPoint);
            jsonObject.put("avatarId",AvatarId);
            output.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
