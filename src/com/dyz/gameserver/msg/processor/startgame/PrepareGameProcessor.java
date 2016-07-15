package com.dyz.gameserver.msg.processor.startgame;

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
 *准备/开始游戏，当所有人都准备好后直接开始游戏
 * @author luck
 *
 */
public class PrepareGameProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public PrepareGameProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
    	RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
    	if(roomLogic != null){
    		Avatar avatar = gameSession.getRole(Avatar.class);
    		if(avatar != null){
    				//检查其他玩家是否都已经是准备好了,准备好了顺带开始游戏，
    				roomLogic.readyGame(avatar);
    			}
    		else{
    			System.out.println("账户未登录或已经掉线!");
				gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000002));
    		}
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }
	}
}
