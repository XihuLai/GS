package com.dyz.gameserver;

import com.context.ErrorCode;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.gameserver.sprite.Character;
import com.dyz.gameserver.sprite.base.GameObj;
import com.dyz.gameserver.sprite.tool.AsyncTaskQueue;
import com.dyz.myBatis.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevin on 2016/6/18.
 */
public class Avatar implements GameObj {
    public AvatarVO avatarVO;
    //请求吃
    public boolean chiQuest = false;
    //存储吃牌的信息
    public CardVO cardVO = new CardVO();
    //请求碰
    public boolean pengQuest = false;
    //请求杠
    public boolean gangQuest = false;
    //请求胡
    public boolean huQuest = false;
    
    //当前玩家能否吃
    public boolean canHu = true;
    
    
    public List<Integer> gangIndex = new  ArrayList<Integer>();
    /**
     * 检测到有人胡牌时存储胡牌的详细消息(划水麻将和长沙麻将用)
     *      0: 天胡
     *            1：过路杠	 1番
					2：暗杠	2番
					3：放杠	3番（谁放牌谁出番）
					4：自摸	4番（其他三家每家出4番）
					5：普通点炮	5番（谁放炮谁出番）
					6：七对点炮	5*3番（谁放炮谁出番）
					7：七对自摸	4*3番（其他三家每家出12番）
					8：杠开	4*3番（其他三家每家出12番）
					9：抢杠	5*3番（谁要杠牌谁出番）
					10：一炮双响	根据胡牌者的牌型来计算放炮者出的番数（胡牌两方所胡牌型的番数相加）
					11：一炮三响	根据胡牌者的牌型来计算放炮者出的番数（同上）
					
				数组格式   牌索引:类型
				放弃的时候需要清空
					
     */
    public List<String> huAvatarDetailInfo = new ArrayList<String>();
    
    

	public CardVO getCardVO() {
		return cardVO;
	}

	public void setCardVO(CardVO cardVO) {
		this.cardVO = cardVO;
	}

	public RoomVO getRoomVO() {
        return roomVO;
    }

    public void setRoomVO(RoomVO roomVO) {
        this.roomVO = roomVO;
        if(avatarVO != null){
            avatarVO.setRoomId(roomVO.getRoomId());
        }
    }

    private RoomVO roomVO;
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
     * list里面字符串规则 
     * 杠：uuid(出牌家),介绍(明杠，暗杠)  （123，明杠）
     * 自己摸来杠：介绍(明杠，暗杠)
     * 
     * 点炮：uuid(出牌家),介绍(胡的类型) （123，qishouhu）
     * 自摸：介绍(胡的类型)
     * 
     * 碰：
     * 
     * eg:  碰： key:1     value: 碰的牌的下标
     * 			杠：key:2    value: 杠的牌的下标
     * 			胡：key:3    value: 胡的牌的下标
     *         吃：key:4    value:  吃的牌的下标(1:2:3)
     * 
     * key:1:碰    2:杠    3:胡   4:吃   5:抢胡
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
    }

    /**
     * 檢測是否可以碰 大于等于2张且对应的下标不为1都可以碰
     * @param cardIndex
     * @return
     */
    public boolean checkPeng(int cardIndex){
    	System.out.println("杠了的牌="+cardIndex+"=="+resultRelation.get(2));
    	System.out.println("碰了的牌="+cardIndex+"=="+resultRelation.get(1));
        if(avatarVO.getPaiArray()[0][cardIndex] >= 2 ){
        	if(resultRelation.get(1) == null ){
        		return true;
        	}
        	else{
        		if(	resultRelation.get(1).contains(cardIndex+"")){
        			return false;
        		}
        		else{
        			return true;
        		}
        	}
        }
        return false;
    }

    /**
     * 檢測是否可以杠别人出的牌/此牌对应的下标不为1（碰过了的牌）
     * @param cardIndex
     * @return
     */
    public boolean checkGang(int cardIndex){
    	System.out.println("杠了的牌="+cardIndex+"=="+resultRelation.get(2));
    	System.out.println("碰了的牌="+cardIndex+"=="+resultRelation.get(1));
        if(avatarVO.getPaiArray()[0][cardIndex] == 3){
        	if(resultRelation.get(1) ==null){
        		return true;
        	}else{
        		if(resultRelation.get(1).contains(cardIndex+"")){
        			return false;
        		}
        		else{
        			System.out.println(resultRelation.get(1));
        			return true;
        		}
        	}
        }
        return false;
    }
    /**
     * 检测当前自己的牌是否可杠
     * @param 
     * @return
     */
    public boolean checkSelfGang(){
    	//剔除掉当前以前吃，碰，杠的牌组 再进行比较
    	boolean flag = false;
    	System.out.println("杠了的牌==="+resultRelation.get(2));
    	System.out.println("碰了的牌==="+resultRelation.get(1));
    	for (int i= 0 ; i <avatarVO.getPaiArray()[0].length ; i++) {
    		if (avatarVO.getPaiArray()[0][i] == 4) {
    			if(resultRelation.get(1) != null && resultRelation.get(1).contains(i+"")){
    				if(resultRelation.get(2) == null){
    					gangIndex.add(i);
    					return true;
    				}
    				else{
    					if(resultRelation.get(2).contains(i+"")){
    						return false;
    					}
    					else{
    						gangIndex.add(i);
    						return true;
    					}
    				}
    				
    				/*if(resultRelation.get(2) != null && resultRelation.get(2).contains(i+"") ){
    					flag = false;
    				}
    				else{
    					gangIndex.add(i);
    					flag = true;
    				}*/
    			}else{
    				//(划水麻将分过路杠和暗杠)
    				gangIndex.add(i);
    				flag = true;
    			}
			}
		}
        return flag;
    }
    /**
     * 檢測是否可以吃
     * @param cardIndex
     * @return
     */
    public boolean checkChi(int cardIndex){
    	boolean flag = false;
    	//只有长沙麻将有吃的打法
    	System.out.println("判断吃否可以吃牌-----cardIndex:"+cardIndex);
    	/**
    	 * 这里检测吃的时候需要踢出掉碰 杠了的牌****
    	 */
    	int []  cardList = avatarVO.getPaiArray()[0];
    	if(cardIndex>=0  && cardIndex <=8){
    		if(cardIndex == 0 && cardList[1] >=1 && cardList[2] >=1 ){
    			flag = true;
    		}
    		else if(cardIndex == 1 && ((cardList[0] >=1 && cardList[2] >=1) 
    				|| (cardList[3] >=1 && cardList[2] >=1))){
    			flag = true;
    		}
    		else if(cardIndex ==8 && cardList[7] >=1 && cardList[6] >=1){
    			flag = true;
    		}
    		else if(cardIndex ==7 && ((cardList[8] >=1 && cardList[6] >=1)
    				|| (cardList[5] >=1 && cardList[6] >=1))){
    			flag = true;
    		}
    		else if(cardIndex >=11 && cardIndex <= 15){
    		  if((cardList[cardIndex-1] >=1 && cardList[cardIndex+1] >=1)
    				|| (cardList[cardIndex-1] >=1 && cardList[cardIndex-2] >=1) 
    				|| (cardList[cardIndex+1] >=1 && cardList[cardIndex+2] >=1)){
    			    flag = true;
    		  }
    		}
    	}
    	else if((cardIndex>=9 && cardIndex <=17)){
    		if(cardIndex == 9 && cardList[10] >=1 && cardList[11] >=1 ){
    			flag = true;
    		}
    		else if(cardIndex == 10 && ((cardList[9] >=1 && cardList[11] >=1) 
    				|| (cardList[11] >=1 && cardList[12] >=1))){
    			flag = true;
    		}
    		else if(cardIndex ==17 && cardList[16] >=1 && cardList[15] >=1){
    			flag = true;
    		}
    		else if(cardIndex ==16 && ((cardList[15] >=1 && cardList[17] >=1)
    				|| (cardList[14] >=1 && cardList[15] >=1))){
    			flag = true;
    		}
    		else if(cardIndex >=11 && cardIndex <= 15){
    		  if((cardList[cardIndex-1] >=1 && cardList[cardIndex+1] >=1)
    				|| (cardList[cardIndex-1] >=1 && cardList[cardIndex-2] >=1) 
    				|| (cardList[cardIndex+1] >=1 && cardList[cardIndex+2] >=1)){
    			flag = true;
    		  }
    		}
    	}
    	else if(cardIndex>=18 && cardIndex <=27){
    		if(cardIndex == 18 && cardList[19] >=1 && cardList[20] >=1 ){
    			flag = true;
    		}
    		else if(cardIndex == 19 && ((cardList[18] >=1 && cardList[20] >=1) 
    				|| (cardList[20] >=1 && cardList[21] >=1))){
    			flag = true;
    		}
    		else if(cardIndex ==27 && cardList[26] >=1 && cardList[25] >=1){
    			flag = true;
    		}
    		else if(cardIndex ==26 && ((cardList[25] >=1 && cardList[27] >=1)
    				|| (cardList[24] >=1 && cardList[25] >=1))){
    			flag = true;
    		}
    		else if(cardIndex >=20 && cardIndex <= 25){
	    		 if((cardList[cardIndex-1] >=1 && cardList[cardIndex+1] >=1)
	    			|| (cardList[cardIndex-1] >=1 && cardList[cardIndex-2] >=1) 
	    			|| (cardList[cardIndex+1] >=1 && cardList[cardIndex+2] >=1)){
	    			 flag = true;
    		  } 
    		}
    	}
        return flag;
    }

    /**
     * 为自己的牌组里加入新牌
     * /碰 1  杠2  胡3  吃4
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
     * 设置牌的状态
     * @param cardIndex
     * @param type;//碰 1  杠2  胡3  吃4
     */
    public void setCardListStatus(int cardIndex,int type){
        avatarVO.getPaiArray()[1][cardIndex] = type;
    }

    /**
     * 把牌从自己的牌列中去除
     * @param cardIndex
     */
    public void pullCardFormList(int cardIndex){
        if(avatarVO.getPaiArray()[0][cardIndex]>0) {
            avatarVO.getPaiArray()[0][cardIndex]--;
        }else{/*
            try {
                session.sendMsg(new ErrorResponse(ErrorCode.Error_000007));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Error : pullCardFormList --> 牌数组里没有这张牌");
        */}
    }

    /**
     * 得到牌组的一维数组。用来判断是否胡牌和听牌用
     * @return
     */
    public int[][] getPaiArray(){
            return avatarVO.getPaiArray();
    }

    public int[] getSinglePaiArray() {
        return avatarVO.getPaiArray()[0];
    }

    public String printPaiString(){
        String sb = "";
        for(int a=0;a<2;a++) {
             sb += "[";
            for (int i = 0; i < avatarVO.getPaiArray()[a].length; i++) {
                sb += avatarVO.getPaiArray()[a][i]+",";
            }
            sb += "]";
        }
        return sb;
    }
    @Override
    public void destroyObj() {
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
