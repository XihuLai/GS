package test.java;

import com.context.ConnectAPI;
import com.dyz.gameserver.pojo.LoginVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.*;
import java.net.Socket;

public class Client {
	public static final String IP_ADDR = "localhost";//服务器地址 
	public static final int PORT = 10122;//服务器端口号  
	
    public static void main(String[] args) {  
        System.out.println("客户端启动...");  
        System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");


		String adaf = JsonUtilTool.toJson(new RoomVO());
		System.out.println("String adaf -- >"+adaf);
		String asdf = "{\n" +
				"  \"hong\": true,\n" +
				"  \"ma\": 2,\n" +
				"  \"name\": \"\",\n" +
				"  \"roomId\": 123,\n" +
				"  \"roomType\": 1,\n" +
				"  \"roundNumber\": 0,\n" +
				"  \"sevenDouble\": false,\n" +
				"  \"ziMo\": 0\n" +
				"}";
		RoomVO s = (RoomVO)JsonUtilTool.fromJson(asdf,RoomVO.class);
		System.out.print("");



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
	            System.out.print("请输入: \t");
	            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();

				LoginVO loginVO = new LoginVO();
				loginVO.setOpenId(str);
				//登录操作，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值    消息处理方式
				ClientSendRequest loginSend = new ClientSendRequest(ConnectAPI.LOGIN_REQUEST);
				loginSend.output.writeUTF(JsonUtilTool.toJson(loginVO));
				out.write(loginSend.entireMsg().array());//
				
				serverCallBack(input);
				
				RoomVO testData = new RoomVO();
				String xx = new BufferedReader(new InputStreamReader(System.in)).readLine();
				/*String ssss;
				System.out.print("请创建房间的规则 1：局数: \t");
				ssss = new BufferedReader(new InputStreamReader(System.in)).readLine();
				testData.setRoundNumber(Integer.parseInt(ssss));
				System.out.print("请创建房间的规则 2：--》 1-自摸胡 ， 2-抢杠胡\t");
				ssss = new BufferedReader(new InputStreamReader(System.in)).readLine();
				testData.setZiMo(Integer.parseInt(ssss));
				System.out.print("请创建房间的规则 3：--》3-是否红中癞子 \t");
				ssss = new BufferedReader(new InputStreamReader(System.in)).readLine();
				testData.setHong(Boolean.parseBoolean(ssss));
				System.out.print("请创建房间的规则 2：--》4-可胡七小对 \t");
				ssss = new BufferedReader(new InputStreamReader(System.in)).readLine();
				testData.setSevenDouble(Boolean.parseBoolean(ssss));
				System.out.print("请创建房间的规则 2：--》5-抓码数 \t");
				ssss = new BufferedReader(new InputStreamReader(System.in)).readLine();
				testData.setMa(Integer.parseInt(ssss));*/
				testData.setHong(false);
				testData.setMa(1);
				testData.setName("test");
				testData.setRoomId(1);
				testData.setRoomType(2);
				testData.setRoundNumber(2);
				testData.setSevenDouble(false);
				testData.setZiMo(1);

				String aa = JsonUtilTool.toJson(testData);
				//创建房间，不同操作不同的ConnectAPI.CREATEROOM_REQUEST值  消息处理方式
				ClientSendRequest sendRequest = new ClientSendRequest(ConnectAPI.CREATEROOM_REQUEST);
				sendRequest.output.writeUTF(aa);
				out.write(sendRequest.entireMsg().array());

				serverCallBack(input);
				
				String xxxx = new BufferedReader(new InputStreamReader(System.in)).readLine();
				String ss = JsonUtilTool.toJson(asdf);
				ClientSendRequest joinroom = new ClientSendRequest(ConnectAPI.JOIN_ROOM_REQUEST);
				sendRequest.output.writeUTF(ss);
				out.write(joinroom.entireMsg().array());

				serverCallBack(input);



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
        }  
    }

	public static void serverCallBack(DataInputStream input){
		try {
			System.out.println("服务器端返回过来的是: " );
			input.readByte();
			int len = input.readInt();
			System.out.println(len);
			int code = input.readInt();
			System.out.println(code);
			String ret = input.readUTF();
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