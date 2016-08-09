package com.dyz.gameserver.msg.processor.login;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
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
				//断线超过时间后，自动退出
				avatar = new Avatar();
				AvatarVO avatarVO = new AvatarVO();
				avatarVO.setAccount(account);
				avatar.avatarVO = avatarVO;
				//把session放入到GameSessionManager
				GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
				loginAction(gameSession,avatar);
				/*boolean flag = GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
				if(flag) {
					loginAction(gameSession,avatar);
				}else{
					gameSession.sendMsg(new LoginResponse(0,null));
					TimeUitl.delayDestroy(gameSession,500);
				}*/
				System.out.println("GameSessionManager getVauleSize -- >" +GameSessionManager.getInstance().getVauleSize());
			}else{
				//断线重连
				GameServerContext.remove_offLine_Character(avatar);
				GameServerContext.add_onLine_Character(avatar);
				avatar.avatarVO.setIsOnLine(true);
				TimeUitl.stopAndDestroyTimer(avatar);
				avatar.setSession(gameSession);
				System.out.println("用户回来了，断线重连，中止计时器");
				//返回用户断线前的房间信息******
				gameSession.setLogin(true);
				gameSession.setRole(avatar);
				returnBackAction(avatar);
				//把session放入到GameSessionManager
				GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
			}
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
	
	/**
	 *玩家断线重连操作
	 * @param gameSession
	 * @param avatar
     */
	public void returnBackAction(Avatar avatar){
		
		RoomLogic roomLogic = RoomManager.getInstance().getRoom(avatar.avatarVO.getRoomId());
		if(roomLogic !=null){
			//如果用户是在玩游戏的时候短信，房间还未被解散，则需要返回游戏房间其他用户信息，牌组信息
			roomLogic.returnBackAction(avatar);
		}
		else{
			//如果不是在游戏时断线，则直接返回个人用户信息avatar
			avatar.getSession().sendMsg(new LoginResponse(1, avatar.avatarVO));
		}
		
	}

}
