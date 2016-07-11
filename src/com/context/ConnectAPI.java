package com.context;

public class ConnectAPI {

	public ConnectAPI() {
		// TODO Auto-generated constructor stub
	}

	
	public static int LOGIN_REQUEST = 0x000001;
	public static int LOGIN_RESPONSE = 0x000002;

	public static int JOIN_ROOM_REQUEST  = 0x000003;
	public static int JOIN_ROOM_RESPONSE  = 0x000004;
	public static int JOIN_ROOM_NOICE = 0x10a004;
	

	public static int SIGNUP_REQUEST = 0x000005;
	public static int SIGNUP_RESPONSE = 0x000006;

	public static int OPENAPP_REQUEST = 0x000007;
	public static int OPENAPP_RESPONSE =0x000008;

	public static int CREATEROOM_REQUEST = 0x00009;
	public static int CREATEROOM_RESPONSE = 0x00010;
	//开始游戏
	public static int STARTGAME_RESPONSE = 0x00012;

	public static int OUT_ROOM_REQUEST  = 0x000013;
	public static int OUT_ROOM_RESPONSE  = 0x000014;
	//出牌
	public static int CHUPAI_REQUEST = 0x100001;
	public static int CHUPAI_RESPONSE = 0x100002;
	//摸牌
	public static int PICKCARD_REQUEST = 0x100003;
	public static int PICKCARD_RESPONSE = 0x100004;
	//碰牌
	public static int PENGPAI_REQUEST = 0x100005;
	public static int PENGPAI_RESPONSE = 0x100006;
	//杠牌
	public static int GANGPAI_REQUEST = 0x100007;
	public static int GANGPAI_RESPONSE = 0x100008;
	public static int OTHER_GANGPAI_NOICE = 0x10a008;
	//胡牌
	public static int HUPAI_REQUEST = 0x100009;
	public static int HUPAI_RESPONSE = 0x100010;
	//吃
	public static int CHIPAI_REQUEST = 0x100011;
	public static int CHIPAI_RESPONSE = 0x100012;
	//其它人摸牌了
	public static  int OTHER_PICKCARD_RESPONSE = 0x100014;

	//放弃请求
	public static int GAVEUP_REQUEST = 0x100015;

	//游戏错误码返回
	public static int ERROR_RESPONSE = 0xffff09;


}
