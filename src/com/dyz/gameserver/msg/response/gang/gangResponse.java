package com.dyz.gameserver.msg.response.gang;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.GangBackVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author luck
 *
 */
public class GangResponse extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param
     */
    public GangResponse(int status, int fristPoint,int nextPoint) {
        super(status, ConnectAPI.GANGPAI_RESPONSE);
        if(status >0){
            GangBackVO gangBackVO = new GangBackVO();
            List<CardVO> cardVOList = new ArrayList<CardVO>();
            //=========================================
            if(fristPoint > 0) {
                CardVO cardVO1 = new CardVO();
                cardVO1.setCardPoint(fristPoint);
                cardVOList.add(cardVO1);
            }
            //=========================================
            if(nextPoint > 0) {
                CardVO cardVO2 = new CardVO();
                cardVO2.setCardPoint(nextPoint);
                cardVOList.add(cardVO2);
            }
            //=========================================
            gangBackVO.setCardList(cardVOList);
            try {
                output.writeUTF(JsonUtilTool.toJson(gangBackVO));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entireMsg();
    }
}
