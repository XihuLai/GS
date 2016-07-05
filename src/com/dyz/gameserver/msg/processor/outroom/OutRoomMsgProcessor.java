package com.dyz.gameserver.msg.processor.outroom;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomResponse;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.JsonUtilTool;

public class OutRoomMsgProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public OutRoomMsgProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		if(GlobalUtil.checkIsLogin(gameSession)) {
			RoomVO roomVO = JsonUtilTool.fromJson(request.getString(),RoomVO.class);
			System.out.println(roomVO.getRoomId());
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomVO.getRoomId());
				if (roomLogic != null) {
					//退出房间
					roomLogic.exitRoom(avatar);
					avatar.getSession().sendMsg(new OutRoomResponse(1, roomVO));
				} else {
					System.out.println("房间号有误");
					gameSession.sendMsg(new JoinRoomResponse(0, null));
				}
			}else{
				
				
			}
			System.out.println("roomId --> " + roomVO.getRoomId());
			System.out.println("gameSession.isLogin() --> " + gameSession.isLogin());
			System.out.println("gameSession.isLogin() --> " + gameSession.getRole(Avatar.class).getUuId());
		}
	}

}
