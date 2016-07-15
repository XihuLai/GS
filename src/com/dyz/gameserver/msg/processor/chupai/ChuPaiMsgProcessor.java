package com.dyz.gameserver.msg.processor.chupai;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.persist.util.JsonUtilTool;

/**
 * Created by kevin on 2016/6/23.
 */
public class ChuPaiMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        //*****
        CardVO cardVO = JsonUtilTool.fromJson(request.getString(),CardVO.class);
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if(roomLogic != null){
            if(cardVO.getCardPoint() == -1){
                gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000009));
            }else{
            	//出牌，发送消息在方法里面
            	roomLogic.chuCard(gameSession.getRole(Avatar.class), cardVO.getCardPoint());
            }
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }

    }
}
