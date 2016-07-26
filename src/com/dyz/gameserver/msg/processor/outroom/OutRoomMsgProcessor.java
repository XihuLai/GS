package com.dyz.gameserver.msg.processor.outroom;

import com.alibaba.fastjson.JSONObject;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.persist.util.GlobalUtil;

/**
 * 
 * @author luck
 *
 */
public class OutRoomMsgProcessor extends MsgProcessor implements
		INotAuthProcessor {

	public OutRoomMsgProcessor() {
	}

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		if(GlobalUtil.checkIsLogin(gameSession)) {
			//RoomVO roomVO = JsonUtilTool.fromJson(request.getString(),RoomVO.class);
			int roomId = Integer.parseInt(request.getString());
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null && roomId != 0) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
				if (roomLogic != null) {
					//退出房间
					roomLogic.exitRoom(avatar,roomId);
					//avatar.getSession().sendMsg(new OutRoomResponse(1, roomVO));
				} else {
					System.out.println("房间号有误");
					JSONObject json = new JSONObject();
					json.put("accountName", "");
					json.put("status_code", "1");
					json.put("error", "房间号有误");
//					accountName:”名字”//退出房间玩家的名字(为空则表示是通知的自己)
//					status_code:”0”//”0”退出成功，”1” 退出失败
//					mess：”消息”
					gameSession.sendMsg(new OutRoomResponse(0, json.toString()));
				}
			}
		}
	}

}
