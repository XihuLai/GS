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
import com.dyz.gameserver.pojo.RoomVO;

import net.sf.json.JSONObject;

/**
 *准备/开始游戏，当所有人都准备好后直接开始游戏
 * @author luck
 *
 */
public class PrepareGameMSGProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public PrepareGameMSGProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		RoomVO  roomVo = gameSession.getRole(Avatar.class).getRoomVO();
		if(roomVo != null ){
			RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomVo.getRoomId());
			if(roomLogic != null){
				Avatar avatar = gameSession.getRole(Avatar.class);
				if(avatar != null){
					String s = request.getString();
					JSONObject json = JSONObject.fromObject(s);
					boolean brun = false;
					boolean bdunla = false;
					if (s.indexOf("run") != -1) {
						brun = (boolean) json.get("run");
					}

					if (s.indexOf("dunla") != -1) {
						bdunla = (boolean)json.get("dunla");
					}

					avatar.avatarVO.setRun(brun);
					avatar.avatarVO.setDunorla(bdunla);

					//检查其他玩家是否都已经是准备好了,准备好了顺带开始游戏，
					roomLogic.readyGame(avatar);
				}
				else{
					//system.out.println("账户未登录或已经掉线!");
					gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000002));
				}
			}else{
				gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
			}
		}
	}
}
