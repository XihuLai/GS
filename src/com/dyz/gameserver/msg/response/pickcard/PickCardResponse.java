package com.dyz.gameserver.msg.response.pickcard;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/23.
 */
public class PickCardResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param
     *  == 100 时表示是询问海底捞还是不捞
     */
    public PickCardResponse(int status,int cardPoint) {
        super(status, ConnectAPI.PICKCARD_RESPONSE);
        CardVO cardVO = new CardVO();
        cardVO.setCardPoint(cardPoint);
        try {
            output.writeUTF(JsonUtilTool.toJson(cardVO));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
