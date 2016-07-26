package com.dyz.gameserver.msg.processor.joinroom;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
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
					boolean joinResult = roomLogic.intoRoom(avatar);
					if(joinResult) {
						System.out.println("加入房间成功");
					}else{
						System.out.println("加入房间失败");
					}
				} else {
					System.out.println("房间号有误");
				}
			}else{
				System.out.println("账户未登录或已经掉线!");
			}
			System.out.println("roomId --> " + roomId);
		}
		else{
			System.out.println("该用户还没有登录");
			gameSession.destroy();
		}
	}

}
