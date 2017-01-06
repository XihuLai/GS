package test.java;

import com.context.ConnectAPI;
import com.dyz.gameserver.pojo.LoginVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.*;
import java.net.Socket;

public class Client2 {
	public static final String IP_ADDR = "localhost";//服务器地址 
	public static final int PORT = 10122;//服务器端口号  
	
    public static void main(String[] args) {  
        System.out.println("客户端启动...");  
        System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");
		while (true) {
        	Socket socket = null;
        	try {
        		//创建一个流套接字并将其连接到指定主机上的指定端口号
	        	socket = new Socket(IP_ADDR, PORT);  
	              
	            //读取服务器端数据  
	            DataInputStream input = new DataInputStream(socket.getInputStream());  
	            //向服务器端发送数据  
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				//ObjectOutputStream  out = new ObjectOutputStream (socket.getOutputStream());
//	            System.out.print("请输入: \t");
//	            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
//				登录测试	            
	            String str = "121";//玩家121 ，123，124，125

				LoginVO loginVO1 = new LoginVO();
				loginVO1.setOpenId(str);
				//登录操作，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值    消息处理方式
				ClientSendRequest loginSend = new ClientSendRequest(ConnectAPI.LOGIN_REQUEST);
				loginSend.output.writeUTF(JsonUtilTool.toJson(loginVO1));
				out.write(loginSend.entireMsg().array());//
				serverCallBack(input);
				
				
				//创建房间测试
//				RoomVO roomVo = new RoomVO();
//				roomVo.setRoundNumber(4);
//				roomVo.setRoomType(4);//鄂尔多斯玩法
//				ClientSendRequest createRoomSend = new ClientSendRequest(ConnectAPI.CREATEROOM_REQUEST);
//				createRoomSend.output.writeUTF(JsonUtilTool.toJson(roomVo));
//				out.write(createRoomSend.entireMsg().array());//
//				serverCallBack(input);//返回317824
				
				//加入房间测试
//				RoomVO roomVo = new RoomVO();
//				roomVo.setRoomId(317824);
//				ClientSendRequest joinRoomSend = new ClientSendRequest(ConnectAPI.JOIN_ROOM_REQUEST);
//				joinRoomSend.output.writeUTF(JsonUtilTool.toJson(roomVo));
//				out.write(joinRoomSend.entireMsg().array());//
//				serverCallBack(input);//返回317824
				
				//游戏开始测试
//				ClientSendRequest gameStartSend = new ClientSendRequest(ConnectAPI.PrepareGame_MSG_REQUEST);
//				out.write(gameStartSend.entireMsg().array());//
//				serverCallBack(input);//返回317824
				
				//庄家出牌测试
//				ClientSendRequest chupaiSend = new ClientSendRequest(ConnectAPI.CHUPAI_REQUEST);
//				joinRoomSend.output.writeUTF(JsonUtilTool.toJson(roomVo));
//				out.write(joinRoomSend.entireMsg().array());//
//				serverCallBack(input);//返回317824
				
				
//				String str2 = "123";//玩家2登录
//
//				LoginVO loginVO2 = new LoginVO();
//				loginVO2.setOpenId(str2);
//				//登录操作，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值    消息处理方式
//				ClientSendRequest loginSend2 = new ClientSendRequest(ConnectAPI.LOGIN_REQUEST);
//				loginSend.output.writeUTF(JsonUtilTool.toJson(loginVO2));
//				out.write(loginSend2.entireMsg().array());//
//				
//				serverCallBack(input);
//				
//				String str3 = "124";//玩家3登录
//
//				LoginVO loginVO3 = new LoginVO();
//				loginVO3.setOpenId(str3);
//				//登录操作，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值    消息处理方式
//				ClientSendRequest loginSend3 = new ClientSendRequest(ConnectAPI.LOGIN_REQUEST);
//				loginSend.output.writeUTF(JsonUtilTool.toJson(loginVO3));
//				out.write(loginSend3.entireMsg().array());//
//				
//				serverCallBack(input);
//				
//				
//				String str4 = "122";//玩家4登录
//
//				LoginVO loginVO4 = new LoginVO();
//				loginVO4.setOpenId(str4);
//				//登录操作，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值    消息处理方式
//				ClientSendRequest loginSend4 = new ClientSendRequest(ConnectAPI.LOGIN_REQUEST);
//				loginSend.output.writeUTF(JsonUtilTool.toJson(loginVO4));
//				out.write(loginSend4.entireMsg().array());//
//				
//				serverCallBack(input);
				
				
				
//				String xxxx = new BufferedReader(new InputStreamReader(System.in)).readLine();
//				String asdf = "{\n" +
//						"  \"hong\": true,\n" +
//						"  \"ma\": 2,\n" +
//						"  \"name\": \"\",\n" +
//						"  \"roomId\": "+xxxx+",\n" +
//						"  \"roomType\": 1,\n" +
//						"  \"roundNumber\": 0,\n" +
//						"  \"sevenDouble\": false,\n" +
//						"  \"ziMo\": 0\n" +
//						"}";
//				String ss = JsonUtilTool.toJson(asdf);
//				ClientSendRequest joinroom = new ClientSendRequest(ConnectAPI.JOIN_ROOM_REQUEST);
//				joinroom.output.writeUTF(ss);
//				out.write(joinroom.entireMsg().array());
//
//				serverCallBack(input);



				out.close();
	            input.close();
        	} catch (Exception e) {
        		System.out.println("客户端异常:" + e.getMessage()); 
        	} finally {
        		if (socket != null) {
        			try {
						socket.close();
					} catch (IOException e) {
						socket = null; 
						System.out.println("客户端 finally 异常:" + e.getMessage()); 
					}
        		}
        	}
        	break;
        }  
    }

	public static void serverCallBack(DataInputStream input){
		try {
			System.out.println("服务器端返回过来的是: " );
			byte byte1 = input.readByte();
			System.out.println(byte1);
			int len = input.readInt();
			System.out.println(len);
			int code = input.readInt();//msgCode
			System.out.println(code);
			int status = input.readInt();
			System.out.println(status);
			String ret = input.readUTF();
//			byte[] result = new byte[600];
//			byte[] result2 = new byte[600];
//			input.read(result);
//			System.arraycopy(result,4,result2,0,591);
//			System.out.println("服务器端返回过来的是: " + new String(result));
			
			System.out.println("服务器端返回过来的是: " + ret);
			// 如接收到 "OK" 则断开连接
			if ("OK".equals(ret)) {
				System.out.println("客户端将关闭连接");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}  