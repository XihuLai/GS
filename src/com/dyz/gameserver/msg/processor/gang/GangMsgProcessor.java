package com.dyz.gameserver.msg.processor.gang;

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
 * 
 * @author luck
 *
 */
public class GangMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).roomVO.getRoomId());
        if(roomLogic != null){
            CardVO cardVO = JsonUtilTool.fromJson(request.getString(),CardVO.class);
           boolean isGang =  roomLogic.gangCard(gameSession.getRole(Avatar.class),cardVO.getCardPoint());
           if(isGang){
        	   //gameSession.sendMsg(new GangResponse(1, "1"));
           }
           else{
        	   System.out.println("碰不起");
           }
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }

    }
}
