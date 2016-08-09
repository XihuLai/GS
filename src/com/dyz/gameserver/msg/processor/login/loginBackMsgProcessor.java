package com.dyz.gameserver.msg.processor.login;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.login.BackLoginResponse;
import com.dyz.persist.util.GlobalUtil;

import net.sf.json.JSONObject;

/**
 * 暂时不用整个类
 * @author luck
 *
 */
public class loginBackMsgProcessor extends MsgProcessor implements INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		
		/*if(GlobalUtil.checkIsLogin(gameSession)) {
			JSONObject json = JSONObject.fromObject(request.getString());
			int roomId = (int)json.get("roomId");
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null) {
				RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomId);
				if (roomLogic != null) {
					gameSession.sendMsg(new BackLoginResponse(1,roomLogic.getRoomVO()));
				} else {
					System.out.println("房间号有误");
					gameSession.sendMsg(new BackLoginResponse(0,ErrorCode.Error_000012));
				}
			}else{
				System.out.println("账户未登录或已经掉线!");
				gameSession.sendMsg(new BackLoginResponse(0,ErrorCode.Error_000002));
			}
		}
		else{
			System.out.println("该用户还没有登录");
			gameSession.destroyObj();
		}*/
	}

}
