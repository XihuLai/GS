package com.dyz.gameserver.msg.response.startgame;

import java.io.IOException;
import java.util.List;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.persist.util.JsonUtilTool;

/**
 * Created by kevin on 2016/6/22.
 */
public class PrepareGameResponse extends ServerResponse {
	/**
	 * 必须调用此方法设置消息号
	 *
	 * @param
	 */
	public PrepareGameResponse(int status, List<AvatarVO> avatars) {
		super(status, ConnectAPI.STARTGAME_RESPONSE);
		try {
			String str = JsonUtilTool.toJson(avatars);
			output.writeUTF(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		entireMsg();
	}
}
