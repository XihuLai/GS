package com.dyz.gameserver.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomResponse;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.RoomVO;

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
        avatar.avatarVO.setRoomId(roomVO.getRoomId());
        avatar.avatarVO.setIsReady(true);
        playerList = new ArrayList<Avatar>();
        avatar.avatarVO.setMain(true);
        playerList.add(avatar);
    }

    /**
     * 进入房间,默认进入准备状态
     * @param avatar
     */
    public boolean intoRoom(Avatar avatar){
        if(playerList.size() == 4){
            avatar.getSession().sendMsg(new JoinRoomResponse(0,ErrorCode.Error_000011));
            return false;
        }else {
            avatar.avatarVO.setMain(false);
            avatar.avatarVO.setRoomId(roomVO.getRoomId());
            avatar.avatarVO.setIsReady(true);
            playerList.add(avatar);
            if(playerList.size() == 4){
            	//当人数4个时自动开始游戏
                //checkCanBeStartGame();当最后一个人加入时，不需要检测其他玩家是否准备(一句结束后开始才需要检测玩家是否准备)
            	startGameRound();
            }
            else{
            	//当有人加入房间时，当人数不够4个时，对其他玩家进行通知
            	sendJoinMess();
            }
            return true;
        }
    }
    /**
     * 当有人加入房间且总人数不够4个时，对其他玩家进行通知
     */
    private void sendJoinMess(){
    	for (int i = 0; i < playerList.size(); i++) {
    		playerList.get(i).getSession().sendMsg(new JoinRoomResponse(1, roomVO));;
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
     * 出牌
     * @return
     */
    public void chuCard(Avatar avatar, int cardIndex){
        playCardsLogic.putOffCard(avatar,cardIndex);
    }

    /**
     * 摸牌
     */
    public int pickCard(Avatar avatar){
        return playCardsLogic.pickCard(avatar);
    }
    /**
     * 吃牌
     * @param avatar
     * @return
     */
    public boolean chiCard(int avatarIndex,int cardIndex){
    	return playCardsLogic.chiCard(avatarIndex,cardIndex);
    }
    /**
     * 碰牌
     * @param avatar
     * @return
     */
    public boolean pengCard(int avatarIndex,int cardIndex){
    	return playCardsLogic.pengCard( avatarIndex, cardIndex);
    }
    /**
     * 杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(int avatarIndex,int cardIndex){
    	return playCardsLogic.gangCard( avatarIndex, cardIndex);
    }
    /**
     * 胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(int avatarIndex,int cardIndex){
    	return playCardsLogic.huPai( avatarIndex, cardIndex);
    	
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
      /* 
       * 上面已经验证过了
       *  if(count <= 0){
            //房间次数用完了,通知所有玩家
        	for (Avatar avatar : playerList) {
        		try {
					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000010) );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        }else{*/
        count--;
        playCardsLogic = new PlayCardsLogic();
        playCardsLogic.setPlayerList(playerList);
        playCardsLogic.initCard(roomVO.getRoomType(),roomVO.getHong());
        
    }

    /**
     * 一局牌结束
     */
    public void roundOver(){
    	
    	
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
}
