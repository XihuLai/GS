package com.dyz.gameserver.msg.response.gang;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by kevin on 2016/7/7.
 */
public class OtherGangResponse extends ServerResponse {
    /**
     *
     *
     * @param status
     * @param avatarId
     * @param cardPoint
     */
    public OtherGangResponse(int status,int cardPoint,int avatarId,int type) {
        super(status, ConnectAPI.OTHER_GANGPAI_NOICE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cardPoint",cardPoint);
        jsonObject.put("avatarId",avatarId);
        jsonObject.put("type",type);
        try {
            output.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
       	 output.close();
		}
        //entireMsg();
    }
}
