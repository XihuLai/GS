package com.dyz.gameserver.msg.processor.contact;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.contact.ContactResponse;
import com.dyz.myBatis.model.ContactWay;
import com.dyz.myBatis.services.ContactWayService;
import com.dyz.persist.util.GlobalUtil;

/**
 * 前段点+号显示联系信息
 * @author luck
 *
 */
public class ContactMsgProcssor extends MsgProcessor implements
        INotAuthProcessor {

    public ContactMsgProcssor(){

    }

    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {

		if(GlobalUtil.checkIsLogin(gameSession)) {
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null) {
				ContactWay contactWay =  ContactWayService.getInstance().selectByPrimaryKey(1);
				gameSession.sendMsg(new ContactResponse(1, contactWay.getContent()) );
			}
		}
		else{
			gameSession.destroyObj();
		}
	
    }
}
