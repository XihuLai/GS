package com.dyz.gameserver.msg.processor.login;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;

public class LoginReturnInfoMsgProcessor extends MsgProcessor implements
INotAuthProcessor {

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		
		RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
    	if(roomLogic != null){
    		Avatar avatar = gameSession.getRole(Avatar.class);
    		if(avatar != null){
    				roomLogic.LoginReturnInfo(avatar);
    			}
    		else{
    			System.out.println("账户未登录或已经掉线!");
    		}
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }
	}

}
