package com.dyz.gameserver.msg.processor.login;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.login.LoginResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.LoginVO;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.services.AccountService;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.JsonUtilTool;
import com.dyz.persist.util.TimeUitl;

public class LoginMsgProcessor extends MsgProcessor implements INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		String message = request.getString();
		LoginVO loginVO = JsonUtilTool.fromJson(message,LoginVO.class);
		Account account = AccountService.getInstance().selectAccount(loginVO.getOpenId());

		if(account==null){
			//创建新用户并登录
			//gameSession.sendMsg(new LoginResponse1004(false));
			account = new Account();
			account.setOpenid(loginVO.getOpenId());
			account.setUuid(GlobalUtil.getRandomUUid());
			account.setRoomcard(3);
			account.setHeadicon(loginVO.getHeadIcon());
			account.setNickname(loginVO.getNickName());
			account.setCity(loginVO.getCity());
			account.setProvince(loginVO.getProvince());
			account.setSex(loginVO.getSex());
			account.setUnionid(loginVO.getUnionid());

			if(AccountService.getInstance().createAccount(account) == 0){
				gameSession.sendMsg(new LoginResponse(0,null));
				System.out.println("创建新用户失败");
				TimeUitl.delayDestroy(gameSession,500);
			}else{
				Avatar tempAva = new Avatar();
				AvatarVO tempAvaVo = new AvatarVO();
				tempAvaVo.setAccount(account);
				tempAva.avatarVO = tempAvaVo;

				loginAction(gameSession,tempAva);
				System.out.println("创建新用户并登录");
			}
		}else{
			//如果玩家是掉线的，则直接从缓存(GameServerContext)中取掉线玩家的信息
			Avatar avatar = GameServerContext.getAvatarFromOff(account.getUuid());
			if(avatar == null) {
				avatar = new Avatar();
				AvatarVO avatarVO = new AvatarVO();
				avatarVO.setAccount(account);
				avatar.avatarVO = avatarVO;
			}else{
				GameServerContext.remove_offLine_Character(avatar);
				GameServerContext.add_onLine_Character(avatar);
				avatar.avatarVO.setIsOnLine(true);
				TimeUitl.stopAndDestroyTimer(avatar);
				System.out.println("用户回来了，断线重连，中止计时器");
			}
			//把session放入到GameSessionManager
			boolean flag = GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
			if(flag) {
				loginAction(gameSession,avatar);
			}else{
				gameSession.sendMsg(new LoginResponse(0,null));
				TimeUitl.delayDestroy(gameSession,500);
			}
			System.out.println("GameSessionManager getVauleSize -- >" +GameSessionManager.getInstance().getVauleSize());
		}
	}

	/**
	 * 登录操作
	 * @param gameSession
	 * @param avatar
     */
	public void loginAction(GameSession gameSession,Avatar avatar){
		gameSession.setRole(avatar);
		gameSession.setLogin(true);
		avatar.setSession(gameSession);
		avatar.avatarVO.setIsOnLine(true);
		GameServerContext.add_onLine_Character(avatar);
		gameSession.sendMsg(new LoginResponse(1,avatar.avatarVO));
	}

}
