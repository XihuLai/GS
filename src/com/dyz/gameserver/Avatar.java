package com.dyz.gameserver;

import com.context.ErrorCode;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.gameserver.sprite.Character;
import com.dyz.gameserver.sprite.base.GameObj;
import com.dyz.gameserver.sprite.tool.AsyncTaskQueue;
import com.dyz.myBatis.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 2016/6/18.
 */
public class Avatar implements GameObj {
    public AvatarVO avatarVO;
    //请求吃
    public boolean chiQuest = false;
    //请求碰
    public boolean pengQuest = false;
    //请求杠
    public boolean gangQuest = false;
    //请求胡
    public boolean huQuest = false;
    public RoomVO roomVO;
    /**
     * session
     */
    private GameSession session;
    /**
     * 牌类别 3-不含字牌，4-含字牌
     */
    private int card_row = 2;
    /**
     * 牌字
     */
    private int card_col = 27;
    /**
     * 日志记录
     */
    private static final Logger logger = LoggerFactory.getLogger(Character.class);
    /**
     * 异步操作队列
     */
    private AsyncTaskQueue asyncTaskQueue = new AsyncTaskQueue();
    /**
     * 存储本局 碰，杠，胡关系
     * list里面字符串规则 
     * 杠：uuid(出牌家),介绍(明杠，暗杠)  （123，明杠）
     * 自己摸来杠：介绍(明杠，暗杠)
     * 
     * 点炮：uuid(出牌家),介绍(胡的类型) （123，qishouhu）
     * 自摸：介绍(胡的类型)
     * 
     * 碰：
     * 
     * eg:  碰： key:1     value:2
     * 			杠：key:2    value:  123(出牌玩家uuid):杠的类型
     * 			胡：key:3    value:  123(出牌玩家uuid):胡的类型
     * 
     * key:1:碰    2:杠    3:胡  
     * value:信息，分条信息之间用","隔开
     */
    private Map<Integer,String> resultRelation = new HashMap<Integer,String>(); 

    public Map<Integer,String> getResultRelation() {
		return resultRelation;
	}
    //存储杠和胡的信息
	public  void putResultRelation(Integer i , String str) {
		synchronized(resultRelation){
			if(resultRelation.get(i) == null){
				resultRelation.put(i, str);
			}
			else{
				resultRelation.put(i, resultRelation.get(i)+","+str);
			}
		}
	}

	public Avatar(){
    }


    public void setQuestToFalse(){
        huQuest = false;
        gangQuest = false;
        pengQuest = false;
        chiQuest = false;
    }
    /**
     * 更新用户数据表信息
     * @param value
     */
    public void updateRoomCard(int value){
        int number = avatarVO.getAccount().getRoomcard();
        number += value;
        avatarVO.getAccount().setRoomcard(number);
        AccountService.getInstance().updateAccount(avatarVO.getAccount());
    }
    /**
     * 获取玩家session
     * @return
     */
    public GameSession getSession() {
        return session;
    }
    /**
     * 获取玩家session
     * @return
     */
    public void setSession(GameSession gameSession) {
         this.session = gameSession;
    }
    /**
     * 添加异步任务，针对异步操作数据库
     * @param tasks
     */
    public void addAsyncTask(Runnable... tasks){
        asyncTaskQueue.addTask(tasks);
    }

    /**
     * 获取用户uuid
     * @return
     */
    public int getUuId(){
        return avatarVO.getAccount().getUuid();
    }


    /**
     * 设置房间属性,然后清理并重新生成新的牌数据
     *   1-转转麻将，2-划水麻将，3-长沙麻将
     */
    public void CreatePaiArray(){
        if(roomVO.getRoomType() == 1){
            card_col = 27;
            if(roomVO.getHong()){
                card_col = 34;
            }
        }else if(roomVO.getRoomType() == 2){
            card_col = 34;

        }else if(roomVO.getRoomType() == 3){
            card_col = 27;
        }
        cleanPaiData();
    }


    /**
     * kevinTest
     */
    public void printPaiArray(){
        int[] pais = getPaiArray();
        System.out.print("[");
        for(int i=0;i<pais.length-1;i++){
            System.out.print(pais[i]+",");
        }
        System.out.print(pais[pais.length -1]);
        System.out.println("]");
    }

    /**
     * 清理玩家的牌数据
     */
    public void cleanPaiData(){
        if(avatarVO.getPaiArray() == null) {
            avatarVO.setPaiArray(new int[card_row][card_col]);
        }
        for(int i=0;i<card_row;i++){
            for(int k=0;k<card_col;k++){
                avatarVO.getPaiArray()[i][k] = 0;
            }
        }
        //TODO kevinTest
        printPaiArray();
    }

    /**
     * 檢測是否可以碰
     * @param cardIndex
     * @return
     */
    public boolean checkPeng(int cardIndex){
        if(avatarVO.getPaiArray()[0][cardIndex] == 2){
            return true;
        }
        return false;
    }

    /**
     * 檢測是否可以杠别人出的牌
     * @param cardIndex
     * @return
     */
    public boolean checkGang(int cardIndex){
        if(avatarVO.getPaiArray()[0][cardIndex] == 3){
            return true;
        }
        return false;
    }
    /**
     * 检测当前自己的牌是否可杠
     * @param 
     * @return
     */
    public boolean checkGang(){
        boolean result = false;
        int [] paiList = getPaiArray();
        for (int i : paiList) {
        		if(i == 4){
        			result = true;
        		}
		}
        
        return result;
    }
    /**
     * 檢測是否可以吃
     * @param cardIndex
     * @return
     */
    public boolean checkChi(int cardIndex){
    	if((cardIndex>=1 && cardIndex <=7) || 
    		(cardIndex>=10 && cardIndex <=16) || 
    		(cardIndex>=19 && cardIndex <=25)){
    		if(avatarVO.getPaiArray()[0][cardIndex-1] >=1 && avatarVO.getPaiArray()[0][cardIndex+1] >=1){
    			return true;
    		}
    	}
        return false;
    }

    /**
     * 为自己的牌组里加入新牌
     * @param cardIndex
     */
    public boolean putCardInList(int cardIndex){
        if(avatarVO.getPaiArray()[0][cardIndex]<4) {
            avatarVO.getPaiArray()[0][cardIndex]++;
            
           return true;
        }else{
            System.out.println("Error : putCardInList --> 牌数组里已经有4张牌");
            try {
                session.sendMsg(new ErrorResponse(ErrorCode.Error_000008));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 把牌从自己的牌列中去除
     * @param cardIndex
     */
    public void pullCardFormList(int cardIndex){
        if(avatarVO.getPaiArray()[0][cardIndex]>0) {
            avatarVO.getPaiArray()[0][cardIndex]--;
        }else{
            try {
                session.sendMsg(new ErrorResponse(ErrorCode.Error_000007));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Error : pullCardFormList --> 牌数组里没有这张牌");
        }
    }

    /**
     * 得到牌组的一维数组。用来判断是否胡牌和听牌用
     * @return
     */
    public int[] getPaiArray(){
            return avatarVO.getPaiArray()[0];
    }


    @Override
    public void destroy() {
    	//统计在线用户****
        logger.info("用户{}断开服务器链接",avatarVO.getAccount().getNickname());
        avatarVO.setPaiArray(null);
        if(session != null){
            System.out.println("session 不为空");
        }else{
            System.out.println("session 以经是空的了");
        }
    }
    /**
     * kevinTest
     * 测试胡牌用的数据
     * @return
     */
    public int[] getTestPaiArray(){
        return new int[]{0, 2, 2, 2, 4, 1, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

}
