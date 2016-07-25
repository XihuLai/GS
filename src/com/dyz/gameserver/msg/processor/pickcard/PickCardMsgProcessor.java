package com.dyz.gameserver.msg.processor.pickcard;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;

/**
 * Created by kevin on 2016/6/23.
 */
public class PickCardMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {

    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        
    	
    	RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if(roomLogic != null ){
            roomLogic.pickCard();
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }
    }
}
