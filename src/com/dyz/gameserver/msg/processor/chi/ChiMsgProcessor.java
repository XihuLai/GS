package com.dyz.gameserver.msg.processor.chi;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.chi.ChiResponse;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.persist.util.JsonUtilTool;

/**
 * 
 * @author luck
 *
 */
public class ChiMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if(roomLogic != null){
           CardVO cardVO = JsonUtilTool.fromJson(request.getString(),CardVO.class);
           boolean isChi =  roomLogic.chiCard(gameSession.getRole(Avatar.class),cardVO);
           if(isChi){
        	   gameSession.sendMsg(new ChiResponse(1, "1"));
           }
           else{
        	   System.out.println("吃不起");
           }
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }

    }
}
