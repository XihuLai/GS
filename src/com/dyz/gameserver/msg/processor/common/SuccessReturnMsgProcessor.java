package com.dyz.gameserver.msg.processor.common;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;


/**
 * 每次后台向前段发送消息的时候，前端如果收到则调用整个接口返回，其接收到的avatarId
 * 后台如果没收到，则再向前段发送一次消息
 * @author luck
 *
 */
public  class SuccessReturnMsgProcessor extends MsgProcessor implements
INotAuthProcessor  {

	@Override
	public void process(GameSession gameSession,ClientRequest request) throws Exception {
		RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if(roomLogic != null){
        	roomLogic.shakeHandsMsg(gameSession.getRole(Avatar.class));
        }
	}
}
