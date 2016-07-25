package com.dyz.gameserver.logic;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomNoice;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomResponse;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.msg.response.startgame.PrepareGameResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.HuReturnObjectVO;
import com.dyz.gameserver.pojo.RoomVO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kevin on 2016/6/18.
 * 房间逻辑
 */
public class RoomLogic {
    private List<Avatar> playerList;
    private boolean isBegin = false;
    private  Avatar createAvator;
    private RoomVO roomVO;
    private PlayCardsLogic playCardsLogic;
    
    /**
     * 房间属性 1-为普通房间
     */
    private int roomType = 1;
    /**
     * 是否添加字牌
     */
    private boolean addWordCard = false;
    /**
     * 房间使用次数
     */
    private int count=0;
    public RoomLogic(RoomVO roomVO){
        this.roomVO = roomVO;
        count = roomVO.getRoundNumber();
    }

    /**
     * 创建房间,默认进入装备状态
     * @param avatar
     */
    public void CreateRoom(Avatar avatar){
        createAvator = avatar;
        roomVO.setPlayerList(new ArrayList<AvatarVO>());
        avatar.avatarVO.setIsReady(true);
        playerList = new ArrayList<Avatar>();
        avatar.avatarVO.setMain(true);
        avatar.setRoomVO(roomVO);
        playerList.add(avatar);
        roomVO.getPlayerList().add(avatar.avatarVO);
    }

    /**
     * 进入房间,默认进入准备状态
     * @param avatar
     */
    public boolean intoRoom(Avatar avatar){
        if(playerList.size() == 4){
            //avatar.getSession().sendMsg(new JoinRoomResponse(0,ErrorCode.Error_000011));
            return false;
        }else {
            avatar.avatarVO.setMain(false);
            avatar.avatarVO.setIsReady(true);
            avatar.avatarVO.setRoomId(roomVO.getRoomId());//房间号也放入avatarvo中
            avatar.setRoomVO(roomVO);
            noticJoinMess(avatar);//通知房间里面的其他几个玩家
            playerList.add(avatar);
            roomVO.getPlayerList().add(avatar.avatarVO);
            avatar.getSession().sendMsg(new JoinRoomResponse(1, roomVO));

            if(playerList.size() == 4){
            	//当人数4个时自动开始游戏
                //checkCanBeStartGame();当最后一个人加入时，不需要检测其他玩家是否准备(一句结束后开始才需要检测玩家是否准备)
                Timer timer = new Timer();
                TimerTask tt=new TimerTask() {
                    @Override
                    public void run() {
                        startGameRound();
                    }
                };
                timer.schedule(tt, 1000);
            }
            return true;
        }
    }
    /**
     * 当有人加入房间且总人数不够4个时，对其他玩家进行通知
     */
    private void noticJoinMess(Avatar avatar){
    	for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).getSession().sendMsg(new JoinRoomNoice(1,avatar.avatarVO));
		}
    }
    
    /**
     * 检测是否可以开始游戏
     * @throws IOException 
     */
    public void checkCanBeStartGame() throws IOException{
    	if(playerList.size() == 4){
    		//房间里面4个人且都准备好了则开始游戏
    		List<AvatarVO> avatarVos = new ArrayList<>();
    		for(int i=0;i<playerList.size();i++){
    			if(playerList.get(i).avatarVO.getIsReady() == false){
    				//还有人没有准备
    				avatarVos.add(playerList.get(i).avatarVO);
    			}
    		}
    		if(avatarVos.size() == 0){
    			if(count <= 0){
    				//房间次数已经为0
    				for (Avatar avatar : playerList) {
    					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000010));
    				}
    				return;
    			}
    			isBegin = true;
    			//所有人都准备好了
    			startGameRound();
    		}
    	}
    }

    /**
     * 退出房间
     * @param avatar
     */
    public void exitRoom(Avatar avatar){
        avatar.avatarVO.setRoomId(0);
        playerList.remove(avatar);
        roomVO.getPlayerList().remove(avatar.avatarVO);
        for (Avatar ava : playerList) {
			//通知房间里面的其他玩家
        	ava.getSession().sendMsg(new OutRoomResponse(1, roomVO));
		}
    }

    /**
     * 销毁房间
     */
    public void destoryRoom(){

    	
    	
    }
    /**
     * 玩家选择放弃操作
     * @param avatar
     * @param type 1-胡，2-杠，3-碰，4-吃
     */
    public void gaveUpAction(Avatar avatar){
        playCardsLogic.gaveUpAction(avatar);
    }

    /**
     * 出牌
     * @return
     */
    public void chuCard(Avatar avatar, int cardIndex){
        playCardsLogic.putOffCard(avatar,cardIndex);
    }

    /**
     * 摸牌
     */
    public void pickCard( ){
        playCardsLogic.pickCard();
    }
    /**
     * 吃牌
     * @param avatar
     * @return
     */
    public boolean chiCard(Avatar avatar,CardVO cardVo){
    	return playCardsLogic.chiCard(avatar,cardVo);
    }
    /**
     * 碰牌
     * @param avatar
     * @return
     */
    public boolean pengCard(Avatar avatar,int cardIndex){
    	return playCardsLogic.pengCard( avatar, cardIndex);
    }
    /**
     * 杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(Avatar avatar,int cardPoint,int gangType){
    	return playCardsLogic.gangCard( avatar, cardPoint,gangType);
    }
    /**
     * 胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar,int cardIndex){
    	return playCardsLogic.huPai( avatar, cardIndex);
    	
    }
    
    /**
     * 游戏准备
     * @param avatar
     * @throws IOException 
     */
    public void readyGame(Avatar avatar) throws IOException{
        if(avatar.avatarVO.getRoomId() != roomVO.getRoomId()){
            System.out.println("你不是这个房间的");
            try {
                avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000006));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        avatar.avatarVO.setIsReady(true);

        checkCanBeStartGame();
    }

    /**
     * 开始一回合新的游戏
     */
    private void startGameRound(){
       
         if(count <= 0){
            //房间次数用完了,通知所有玩家
        	for (Avatar avatar : playerList) {
        		try {
					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000010) );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        }else{
	        count--;
	        playCardsLogic = new PlayCardsLogic();
	        playCardsLogic.setPlayerList(playerList);
	        playCardsLogic.initCard(roomVO);
	       /* int count = 0;
	        if(roomVO.getRoomType() ==1){
	        	//划水麻将，发了牌之后剩余的牌数量
	        	if(roomVO.getHong()){
	        		count = 4*28-13*4-1;
	        	}
	        	else{
	        		count = 4*27-13*4-1;
	        	}
	        }
	        else if(roomVO.getRoomType() ==2){
	        	//划水麻将
	        	
	        }
	        else{
	        	//长沙麻将
	        	
	        }*/
	        //playCardsLogic.updateSurplusCardCount(count);
	        for(int i=0;i<playerList.size();i++){
	        	//清除各种数据  1：本局胡牌时返回信息组成对象 ，
	        	playerList.get(i).avatarVO.setHuReturnObjectVO(new HuReturnObjectVO());
	        	//playerList.get(i).avatarVO.setCanHu(true);
	        	playerList.get(i).huQuest = true;
	            playerList.get(i).getSession().sendMsg(new PrepareGameResponse(1,playerList.get(i).avatarVO.getPaiArray(),playerList.indexOf(playCardsLogic.bankerAvatar)));
	        }
        }
    }
    
    /**
     * 前后端握手消息处理
     * @param avatar
     */
    public void shakeHandsMsg(Avatar avatar){
    	playCardsLogic.shakeHandsMsg(avatar);
    }
    
    

    public RoomVO getRoomVO() {
        return roomVO;
    }

	public List<Avatar> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Avatar> playerList) {
		this.playerList = playerList;
	}

	public int getCount() {
		return count;
	}
	
	
}
