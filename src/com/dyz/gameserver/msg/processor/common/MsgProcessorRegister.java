package com.dyz.gameserver.msg.processor.common;

import com.context.ConnectAPI;
import com.dyz.gameserver.msg.processor.chi.ChiMsgProcessor;
import com.dyz.gameserver.msg.processor.createroom.CreateRoomMsgProcssor;
import com.dyz.gameserver.msg.processor.joinroom.JoinRoomMsgProcessor;
import com.dyz.gameserver.msg.processor.login.LoginMsgProcessor;
import com.dyz.gameserver.msg.processor.login.OpenAppMsgProcessor;
import com.dyz.gameserver.msg.processor.login.SignUpMsgProcessor;
import com.dyz.gameserver.msg.processor.outroom.OutRoomMsgProcessor;
import com.dyz.gameserver.msg.processor.pass.GaveUpMsgProcessor;
import com.dyz.gameserver.msg.processor.peng.PengMsgProcessor;
import com.dyz.gameserver.msg.processor.pickcard.PickCardMsgProcessor;


/**
 * 消息处理器注册类，所有的消息处理器，都在此注册实例化
 * @author dyz
 *
 */
public enum MsgProcessorRegister {
	/**用户打开app*/
	openApp(ConnectAPI.OPENAPP_REQUEST,new OpenAppMsgProcessor()),
	/**登陆处理器*/
	login(ConnectAPI.LOGIN_REQUEST,new LoginMsgProcessor()),
	/**用户注册处理器*/
	signUp(ConnectAPI.SIGNUP_REQUEST,new SignUpMsgProcessor()),
	/**创建 房间*/
	createRoom(ConnectAPI.CREATEROOM_REQUEST,new CreateRoomMsgProcssor()),
	/**进入游戏房间*/
	joinRoom(ConnectAPI.JOIN_ROOM_REQUEST,new JoinRoomMsgProcessor()),
	/**摸牌*/
	pickPai(ConnectAPI.PICKCARD_REQUEST,new PickCardMsgProcessor()),
	/**出牌*/
	chuPai(ConnectAPI.CHUPAI_REQUEST,new ChiMsgProcessor()),
	/**退出房间*/
	outRoom(ConnectAPI.OUT_ROOM_REQUEST,new OutRoomMsgProcessor()),
	/**吃牌*/
	chiPai(ConnectAPI.CHIPAI_REQUEST,new ChiMsgProcessor()),
	/**吃牌*/
	pengPai(ConnectAPI.PENGPAI_REQUEST,new PengMsgProcessor()),
	/**放弃操作*/
	gaveUp(ConnectAPI.GAVEUP_REQUEST,new GaveUpMsgProcessor());

	private int msgCode;
	private MsgProcessor processor;

	/**
	 * 不允许外部创建
	 * @param msgCode
	 * @param processor
     */
	private MsgProcessorRegister(int msgCode,MsgProcessor processor){
		this.msgCode = msgCode;
		this.processor = processor;
	}

	/**
	 * 获取协议号
	 * @return
     */
	public int getMsgCode(){
		return this.msgCode;
	}

	/**
	 * 获取对应的协议解晰类对象
	 * @return
     */
	public MsgProcessor getMsgProcessor(){
		return this.processor;
	}
}
