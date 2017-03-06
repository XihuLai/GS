package com.dyz.gameserver.msg.processor.joinroom;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.persist.util.GlobalUtil;
import net.sf.json.JSONObject;

public class JoinRoomMsgProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public JoinRoomMsgProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		if(GlobalUtil.checkIsLogin(gameSession)) {
			JSONObject json = JSONObject.fromObject(request.getString());
			int roomId = (int)json.get("roomId");
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
				if (roomLogic != null) {
					if(avatar.avatarVO.getRoomId() != 0){
						//avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000017));
						// 在房间则 直接回到房间
						//把session放入到GameSessionManager,并且移除以前的session
						if (gameSession != avatar.getSession()) {
							//应该不会调到这段代码 -- Xihu
							gameSession.setLogin(true);
							gameSession.setRole(avatar);
							avatar.setSession(gameSession);
							GameSessionManager.getInstance().putGameSessionInHashMap(gameSession, avatar.getUuId());
						}
						roomLogic.returnBackAction(avatar);
						return;
					}
					boolean joinResult = roomLogic.intoRoom(avatar);
					if(joinResult) {
//						System.out.println("加入房间成功");
					}else{
//						System.out.println("加入房间失败");
					}
				} else {
					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000018));
				}
			}
			//system.out.println("roomId --> " + roomId);
		}
		else{
			//system.out.println("该用户还没有登录");
			gameSession.destroyObj();
		}
	}

}
