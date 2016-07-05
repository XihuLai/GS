package com.dyz.gameserver.msg.processor.login;


import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.login.SignUpResponse;
import com.dyz.persist.roledata.user.User;
import com.dyz.persist.roledata.user.UserService;

public class SignUpMsgProcessor extends MsgProcessor implements INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		System.out.println("into SignUpMsgProcessor1005");
		//String name = request.getString();
		//String phone = request.getString();
		//String email = request.getString();
		//String passwd = request.getString();

		User user = new User();
		user.setName("12312313");
		user.setPhonenumber("sdasdasdasdas");
		user.setEmail("123123@1sdsdasdasd");
		user.setPasswd("123123");
		user.setRegdate(System.currentTimeMillis());

		UserService.getInstance().insertUser(user);
		if(user.getId()!=null){
			System.out.println("success");
			gameSession.sendMsg(new SignUpResponse(1,true));
		}else{
			System.out.println("failed");
			gameSession.sendMsg(new SignUpResponse(0,false));
		}

	}

}
