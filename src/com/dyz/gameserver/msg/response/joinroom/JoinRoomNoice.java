package com.dyz.gameserver.msg.response.joinroom;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;

/**
 * Created by kevin on 2016/7/8.
 */
public class JoinRoomNoice extends ServerResponse {
    /**
     * 必须调用此方法设置消息号
     *
     * @param status
     * @param avatarVO
     */
    public JoinRoomNoice(int status, AvatarVO avatarVO) {
        super(status, ConnectAPI.JOIN_ROOM_NOICE);
        if(status > 0){
            try {
                output.writeUTF(JsonUtilTool.toJson(avatarVO));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
       // entireMsg();
    }
}
