package com.dyz.gameserver.msg.processor.outroom;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.persist.util.GlobalUtil;

import net.sf.json.JSONObject;

/**
 * 退出房间
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
			JSONObject json = JSONObject.fromObject(request.getString());
			int roomId = (int)json.get("roomId");
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null && roomId != 0) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
				if (roomLogic != null) {
					//退出房间
					roomLogic.exitRoom(avatar);
				} else {
					//system.out.println("房间号有误");
					JSONObject js = new JSONObject();
					js.put("accountName", "");
					js.put("status_code", "1");
					js.put("error", "房间号有误");
//					accountName:”名字”//退出房间玩家的名字(为空则表示是通知的自己)
//					status_code:”0”//”0”退出成功，”1” 退出失败
//					mess：”消息”
					gameSession.sendMsg(new OutRoomResponse(0, json.toString()));
				}
			}
		}
	}

}
