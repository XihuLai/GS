package test.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.NormalHuPai;

public class HuAndScoreTest {

	
	private NormalHuPai normalHuPai = new NormalHuPai();
	public List<Avatar> playerList = new ArrayList<Avatar>();
	boolean followBanke = false;
	public static void main(String[] args) {
		HuAndScoreTest test = new HuAndScoreTest();
		// TODO Auto-generated method stub
		RoomVO roomVO = new RoomVO();
		roomVO.setRoomType(8);
		roomVO.setPldscore(2);
		roomVO.setAddFlowerCard(true);
		roomVO.setAddWordCard(true);
		roomVO.setHunyise(true);
		roomVO.setPengpeng(true);
		roomVO.setQgbkd(true);
		roomVO.setKan5(true);
		Avatar player1 = new Avatar();
		player1.canHu = true;
//		player1.overOff = true;//这个标识位设定庄
		player1.pengQuest = true;//碰代表蹲或啦
		player1.gangQuest = true;//杠代表跑
		player1.oneSettlementInfo = "111111111";
		Avatar player2 = new Avatar();
		player2.oneSettlementInfo = "222222222";
		player2.overOff = true;//这个标识位设定庄
		Avatar player3 = new Avatar();
		player3.oneSettlementInfo = "333333333";
		Avatar player4 = new Avatar();
		player4.oneSettlementInfo = "444444444";
		test.playerList.add(player1);
		test.playerList.add(player2);
		test.playerList.add(player3);
		test.playerList.add(player4);
		int[][] paiList = new int[2][42];
//		paiList[0]  = new int[]{0,0,0,3,0,0,1,1,1,    0,1,1,1,0,0,0,0,0,    0,0,2,0,0,0,1,1,1,   0,0,0,0,0,0,0,};
//		paiList[0]  = new int[]{0,0,2,2,2,2,2,2,2,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,  1,2,3,4,1,2,3,4};
//		paiList[0]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    4,0,4,1,1,1,1,1,4,   0,0,0,0,0,0,0,  0,2,3,1,0,1,3,4};
		paiList[0]  = new int[]{0,0,0,1,1,1,0,2,0,    0,1,1,1,0,0,1,1,1,    0,0,0,3,0,0,0,0,0,    0,0,0,0,0,0,0,  1,2,3,4,1,2,3,4};
		paiList[1]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0};
//		paiList[1]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,};
//    	Map<String,Integer> result = test.checkHu(paiList,player1,26);
//		int result = test.calculateScore(paiList,player1,roomVO,23,1);
		boolean result = test.checkHu(paiList,100);
//		int result = test.checkSevenDouble(paiList);
		System.out.println(result);

//    	boolean resultting = test.checkSelfTing(paiList,true);
//    	System.out.println(resultting);
	}
	
	
	public int calculateScore(int[][] paiList,Avatar avatar,RoomVO roomVO,int cardIndex,int dian){
    	int score = 0;
    	int totalScore = 0;
    	String recordType = "";//胡牌的分类
    	
//    	score+=1;//基本胡牌分数
//		if(avatar.getUuId()==bankerAvatar.getUuId())//庄家胡
//			score+=1;

		
    	int pldscore = roomVO.getPldscore();
		Map<String,Integer> huResult = checkHu(paiList,avatar , cardIndex);//算好所有的名堂
		int roomType = roomVO.getRoomType();
		if(roomType==4||roomType==5||roomType==6||roomType==7){//鄂尔多斯，呼和浩特和集宁玩法
			int roomScore = 5;
				if(roomType==7)	
					roomScore = 9;
			if(!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_menqing))//门清
				score+=1;
			if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen))//财神
				score+=huResult.get(Rule.CaiShen);
			//特色选项
			if(roomVO.isQgbkd()&&huResult.containsKey(Rule.Hu_quemen))//缺门
				score+=1;
			if(roomVO.isQgbkd()&&huResult.containsKey(Rule.Hu_qingyise)&&!huResult.containsKey(Rule.Hu_yitiaolong)&&huResult.containsKey(Rule.Hu_gouzhang))//够张
				score+=1;
			if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5())//坎五万
				score+=roomScore;
			if(roomVO.isQgbkd()&&!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan))//边砍钓
				score+=1;
			//处理胡牌方法
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1)//清一色
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_qxd))//七小对
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_yitiaolong))//一条龙
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng())//碰碰胡
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise())//混一色
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_haohuaqxd))//豪华七对
				score+=10;
			if(huResult.containsKey(Rule.Hu_shisanyao))//十三幺
				score+=13;
			
			//上面的score为固定分数
				
				boolean dunorla = avatar.pengQuest;
				boolean pao = avatar.gangQuest;
				for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
					int calscore = 0;
					if(!player.oneSettlementInfo.equals(avatar.oneSettlementInfo)){//如果不是当前用户
				//处理跑拉蹲分数		
				if(dunorla)
					calscore+=pldscore;
				if(pao)
					calscore+=pldscore;
				boolean curdunorla = avatar.pengQuest;
				boolean curpao = avatar.gangQuest;
				if(curdunorla)
					calscore+=pldscore;
				if(curpao)
					calscore+=pldscore;
				
				
				if(followBanke){//为连庄
					calscore+=1;
					
				}else{//为拉庄
					if(player.overOff)//如果是被拉庄了，那么多出一分
						calscore+=1;
					
				}
				
				if(dian == -1){//为自摸
					recordType = "1";
					//加分项逻辑
					calscore+=2;//自摸加两分
				}else{//为点炮
					if(playerList.indexOf(player)!=dian){
						continue;
					}
					if(roomType==7)
						calscore = 3;
					else
						calscore+=1;//点炮加一分
					
				}
				
				calscore+=score;
				totalScore+=calscore;
				
//				player.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*(score+calscore));
				System.out.println(player.oneSettlementInfo+"输掉"+(calscore));
					}else{//如果是当前用户
						//增加胡家的分数
//						 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, totalScore);
//						 System.out.println(avatar.oneSettlementInfo+"赢了"+totalScore);
					}
				}
				System.out.println(avatar.oneSettlementInfo+"赢了"+totalScore);
			
			
		}else if(roomType == 8){//包头
			int roomScore = 5;
			int multiscore = 1;
		if(huResult.containsKey(Rule.Hu_menqing))//门清加倍
			multiscore*=2;
		if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen))//财神
			score+=huResult.get(Rule.CaiShen);
		//特色选项
		if(huResult.containsKey(Rule.Hu_quemen)&&roomVO.isQgbkd())//缺门
			score+=huResult.get(Rule.Hu_quemen);
		if(huResult.containsKey(Rule.Hu_gouzhang)&&roomVO.isQgbkd())//够张
			score+=1;
		if(huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isQgbkd())//边砍钓
			score+=1;
		if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5())//坎五万
			score+=roomScore;
		//处理胡牌方法
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1)//清一色
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_qxd))//七小对
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_yitiaolong))//一条龙
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng())//碰碰胡
			score+=roomScore;
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise())//混一色
			score+=roomScore;
		if(huResult.containsKey(Rule.Hu_haohuaqxd))//豪华七对
			score+=(roomScore*4);
		if(huResult.containsKey(Rule.Hu_shisanyao))//缺门
			score+=13;
		
		
			
			boolean dunorla = avatar.pengQuest;
			boolean pao = avatar.gangQuest;
			for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
				int calscore = 0;
				int calmulti = 1;
				if(!player.oneSettlementInfo.equals(avatar.oneSettlementInfo)){//如果不是当前用户
			//处理跑拉蹲分数		
			if(dunorla)
				calscore+=pldscore;
			if(pao)
				calscore+=pldscore;
			boolean curdunorla = avatar.pengQuest;
			boolean curpao = avatar.gangQuest;
			if(curdunorla)
				calscore+=pldscore;
			if(curpao)
				calscore+=pldscore;
			
			
			if(followBanke){//为连庄
				calscore+=1;
				
			}else{//为拉庄
				if(player.overOff)//如果是被拉庄了，那么多出一分
					calscore+=1;
				
			}
			
			if(dian == -1){//为自摸
				recordType = "1";
				//加分项逻辑
				calmulti*=2;//自摸加倍
			}else{//为点炮
				if(playerList.indexOf(player)!=dian){
					continue;
				}
				if(roomType==7)
					calscore = 3;
				else
					calscore+=1;//点炮加一分
				
			}
			calscore+=score;
			calmulti*=multiscore;
			calscore*=calmulti;
			totalScore+=calscore;
//			player.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*calscore);
			System.out.println(player.oneSettlementInfo+"输掉"+(calscore));
				}else{//如果是当前用户
					//增加胡家的分数
//					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, totalScore);
					 
				}
			}
			System.out.println(avatar.oneSettlementInfo+"赢了"+(totalScore));
		}
			 
    	return 0;
    }
	

	private Map<String,Integer> checkHu(int[][] paiList,Avatar avatar,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       Map<String,Integer> result = new HashMap<String,Integer>();
     //处理胡牌的逻辑
   			//可七小队
   			int isSeven = checkSevenDouble(paiList.clone());
               if(isSeven == 0){
                   //System.out.println("没有七小对");
            	   if(checkThirteen(paiList.clone())){
            		   result.put("Hu", 1);//
            		   result.put(Rule.Hu_shisanyao, 1);
            	   }else{//常规胡法
            		   if(normalHuPai.checkHu(paiList.clone())){
            			   result.put("Hu", 1);
            		   }else{
            			   result.put("Hu", 0);
            		   }
            		   
            	   }
               }else if(isSeven ==1){
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_qxd, 1);
               }else{
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_haohuaqxd, 1);
               }
               if(result.get("Hu")==1){//开始检查各种名堂
            	   int i = checkQys(paiList.clone());//检查是否清一色
            	   if(i==1)
            		   result.put(Rule.Hu_qingyise, 1);
            	   if(i==2)
            		   result.put(Rule.Hu_hunyise, 1);
            	   if(checkLong(paiList.clone()))//检查是否一条龙
            		   result.put(Rule.Hu_yitiaolong, 1);
            	   int quemen = checkQuemen(paiList.clone());
            	   if(quemen>0)
            	   //检查是否缺够边坎钓
            		   result.put(Rule.Hu_quemen, quemen);
            	   if(checkGouzhang(paiList.clone()))
            		   result.put(Rule.Hu_gouzhang, 1);
            	   if(cardIndex==4&&checkKan5(paiList.clone()))
            		   result.put(Rule.Hu_kanwuwan, 1);
            	   if(checkBkd(paiList.clone(),cardIndex))
            		   result.put(Rule.Hu_biankandiao, 1);
            	   if(checkMenqing2(paiList.clone()))
            		   result.put(Rule.Hu_menqing, 1);
            	   if(checkPengPeng(paiList.clone()))
            		   result.put(Rule.Hu_pengpeng, 1);
            	   int flowers = checkFlower2(paiList.clone(),avatar);
            	   if(flowers>0)
            		   result.put(Rule.CaiShen, flowers);//查看里面花牌的张数
            	   return result;//胡牌了返回结果
               }
               else
            	   return null;//没有胡牌直接返回空
       
       }
	
	
	private Map<String,Integer> checkHu2(Avatar avatar,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       Map<String,Integer> result = new HashMap<String,Integer>();
     //处理胡牌的逻辑
       int [][] paiList =  avatar.getPaiArray();
   			//可七小队
   			int isSeven = checkSevenDouble(paiList.clone());
               if(isSeven == 0){
                   //System.out.println("没有七小对");
            	   if(checkThirteen(paiList.clone())){
            		   result.put("Hu", 1);//
            		   result.put(Rule.Hu_shisanyao, 1);
            	   }else{//常规胡法
            		   if(normalHuPai.checkHu(paiList.clone())){
            			   result.put("Hu", 1);
            		   }else{
            			   result.put("Hu", 0);
            		   }
            		   
            	   }
               }else if(isSeven ==1){
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_qxd, 1);
               }else{
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_haohuaqxd, 1);
               }
               if(result.get("Hu")==1){//开始检查各种名堂
            	   int i = checkQys(paiList.clone());//检查是否清一色
            	   if(i==1)
            		   result.put(Rule.Hu_qingyise, 1);
            	   if(i==2)
            		   result.put(Rule.Hu_hunyise, 1);
            	   if(checkLong(paiList.clone()))//检查是否一条龙
            		   result.put(Rule.Hu_yitiaolong, 1);
            	   int quemen = checkQuemen(paiList.clone());
            	   if(quemen>0)
            	   //检查是否缺够边坎钓
            		   result.put(Rule.Hu_quemen, quemen);
            	   if(checkGouzhang(paiList.clone()))
            		   result.put(Rule.Hu_gouzhang, 1);
            	   if(cardIndex==4&&checkKan5(paiList.clone()))
            		   result.put(Rule.Hu_kanwuwan, 1);
            	   if(checkBkd(paiList.clone(),cardIndex))
            		   result.put(Rule.Hu_biankandiao, 1);
            	   if(checkMenqing(paiList.clone(),avatar))
            		   result.put(Rule.Hu_menqing, 1);
            	   if(checkPengPeng(paiList.clone()))
            		   result.put(Rule.Hu_pengpeng, 1);
            	   int flowers = checkFlower(paiList.clone(),avatar);
            	   if(flowers>0)
            		   result.put(Rule.CaiShen, flowers);//查看里面花牌的张数
            	   return result;//胡牌了返回结果
               }
               else
            	   return null;//没有胡牌直接返回空
       
       }
	private boolean checkHu(int[][] paiList,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       boolean flag = false;
     //处理胡牌的逻辑
       if(cardIndex!=-1&&cardIndex!=100)
    	   paiList[0][cardIndex] +=1;
   			//可七小队
   			int isSeven = checkSevenDouble(paiList.clone());
               if(isSeven == 0){
                   //System.out.println("没有七小对");
            	   if(checkThirteen(paiList.clone())){
            		   flag = true;
            	   }else{//常规胡法
            		   if(normalHuPai.checkHu(paiList.clone())){
            			   flag = true;
            		   }
            		   
            	   }
               }else{
            	   return true;
               }
               if(cardIndex!=-1&&cardIndex!=100)
            	   paiList[0][cardIndex] -=1;
               return flag;
       }
	
    private int checkFlower(int[][] paiList,Avatar avatar){//检查花牌张数
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int indexMain = 0;
    	for(Avatar avator:playerList){
    		if(avator.avatarVO.isMain()){
    			indexMain = playerList.indexOf(avator);//庄家序号
    			break;
    		}
    	}
    	int indexCur = playerList.indexOf(avatar);//自家序号
    	int dis = indexCur - indexMain;
    	if(dis<0)
    		dis = dis+4;
    	int flag = 1+dis;
    	int count = 0;
    	for(int i=27;i<pai.length;i++){
    		if(i>33&&pai[i]==flag){
    			count++;
    		}
    	}
    	return count;
    }
    
    private int checkFlower2(int[][] paiList,Avatar avatar){//检查花牌张数
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int indexMain = 0;
    	for(Avatar avator:playerList){
    		if(avator.canHu){
    			indexMain = playerList.indexOf(avator);//庄家序号
    			break;
    		}
    	}
    	int indexCur = playerList.indexOf(avatar);//自家序号
    	int dis = indexCur - indexMain;
    	if(dis<0)
    		dis = dis+4;
    	int flag = 1+dis;
    	int count = 0;
    	for(int i=27;i<pai.length;i++){
    		if(i>33&&pai[i]==flag){
    			count++;
    		}
    	}
    	System.out.println("花牌有"+count);
    	return count;
    }
    
    
    private boolean checkPengPeng(int[][] paiList){
    	boolean result = true;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int jiang = 0;
    	for(int i=0;i<pai.length;i++){
    		if(i<34&&pai[i]==2){
    			jiang++;
    			if(jiang>1)
    				return false;
    		}else if(i<34&&(pai[i]>=3||pai[i]==0)){
    			continue;
    		}else{
    			return false;
    		}
    	}
    	System.out.println("能胡碰碰胡");
    	return result;
    }
    
    

    
    
    private boolean checkMenqing(int[][] paiList,Avatar avatar){
    	boolean result = true;
    	int[] pai2 = GlobalUtil.CloneIntList(paiList[1]);
    	for(int i=0;i<pai2.length;i++){
    		if((pai2[i]==1||pai2[i]==4)&&i<34){//有吃牌或者碰牌
    			result = false;
    			return result;
    		}
    	}
    		String mingang =avatar.avatarVO.getHuReturnObjectVO().getTotalInfo().get("gang");
    		if(mingang!=null&&!mingang.equals("")){//有明杠
    			result = false;
    			return result;
    		}
    	return result;
    }
    
    private boolean checkMenqing2(int[][] paiList){
    	boolean result = true;
    	int[] pai2 = GlobalUtil.CloneIntList(paiList[1]);
    	for(int i=0;i<pai2.length;i++){
    		if((pai2[i]==1||pai2[i]==4)&&i<34){//有吃牌或者碰牌
    			result = false;
    			return result;
    		}
    	}
//    		List mingang =avatar.avatarVO.getHuReturnObjectVO().getGangAndHuInfos().get("5");
//    		if(mingang!=null&&mingang.size()>0){//有明杠
//    			result = false;
//    			return result;
//    		}
    	System.out.println("能胡门清");
    	return result;
    }
    
    private boolean checkBkd(int[][] paiList,Integer cardIndex){
    	boolean result = false;
    	int[] pai2 =GlobalUtil.CloneIntList(paiList[0]);
    	for(int i=0;i<paiList[0].length&&i<34;i++){
            if(paiList[1][i] == 1 && pai2[i] >= 3) {
                pai2[i] -= 3;
            }else if((paiList[1][i] == 2||paiList[1][i] == 6) && pai2[i] == 4){
                pai2[i]  -= 4;
            }else if(paiList[1][i]/4>0&&paiList[1][i]%4==0 && pai2[i] > 0){//吃牌的标识是4，吃几次扣几次
            	int times = paiList[1][i]/4;
            	pai2[i] = pai2[i]-times;
            }else if(paiList[1][i] == 5 && pai2[i] == 4){//碰一次并且吃一次
                pai2[i]  -= 4;
            }
        }
    	int[] pai =GlobalUtil.CloneIntList(pai2);
    	
    	//判断视否唯一听口
		for(int i=0;i<34;i++){
			pai =GlobalUtil.CloneIntList(pai2);
    		pai[cardIndex]--;
			pai[i]++;
    		if(normalHuPai.isHuPai(pai)&&i!=cardIndex){//有多个听口不算坎
    			return false;
    		}
		}
		
    	int flag = cardIndex/9;
    	//先判断边,分为左边和右边
    	if(cardIndex-2>=flag*9&&pai[cardIndex-1]>0&&pai[cardIndex-2]>0&&cardIndex%9==2){//右边,只有为3的时候
    		pai[cardIndex]--;
    		pai[cardIndex-1]--;
    		pai[cardIndex-2]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex-1]++;
        		pai[cardIndex-2]++;
    		}
    			
    	}else if(cardIndex+2<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex+2]>0&&cardIndex%9==6){//左边，只有为7的时候
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex+2]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex+1]++;
        		pai[cardIndex+2]++;
    		}
    	}
    	//然后判断坎
    	if(cardIndex-1>=flag*9&&cardIndex+1<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex-1]>0){
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex-1]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    			
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex+1]++;
        		pai[cardIndex-1]++;
    		}
    	}
    	//最后判断钓
    	if(pai[cardIndex]>=2){
    		pai[cardIndex]-=2;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			result = true;
    		}
    		normalHuPai.setJIANG(0);
    		return result;
    	}
    	
    	return result;
    }
    
    
    private boolean checkKan5(int[][] paiList){//判断视否坎五万
    	boolean result = false;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	if(pai[3] >= 1 && pai[5] >= 1){
    		pai[3]--;
    		pai[4]--;
    		pai[5]--;
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}
    	if(pai[4] >= 2){
    		pai[4] -=2 ;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			normalHuPai.setJIANG(0);
    			result = true;
    		}
    		//追加判断是否只有单听口
    		normalHuPai.setJIANG(0);
    		for(int i=0;i<34;i++){
    			pai =GlobalUtil.CloneIntList(paiList[0]);
        		pai[4]--;
    			pai[i]++;
        		if(normalHuPai.isHuPai(pai)&&i!=4){//有多个听口不算单吊
        			return false;
        		}else{
        			pai[i]--;
        		}
    		}
    	}
        return result;
    	
    }
    
    private boolean checkGouzhang(int[][] paiList){
    	boolean result = false;
    	int flag=0;
    	for(int i=0;i<27;i++){
    		if(paiList[0][i]>0){
    			flag+=paiList[0][i];
    		}
    		if(i%9==8){
    			if(flag>=8)
    			return true;
    			else{
    				flag = 0;
    				continue;
    			}
    		}
    	}
    	if(result)
    	System.out.println("能胡够张");
    	return result;
    }
    
    private int checkQuemen(int[][] paiList){//检测是否缺门
//    	boolean result = true;
    	int result = 3;
    	for(int i=0;i<9;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	if(!result)
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	else{
//    		return true;
//    	}
//    	if(!result)
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	else{
//    		return true;
//    	}
    	
    		System.out.println("缺门数"+result);
    	return result;
    }
    
    private boolean checkLong(int[][] paiList){//检查是否一条龙
    	boolean result = true;
    	for(int i=0;i<9;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(result){//如果是在万里面有一条龙
    		for(int i=0;i<9;i++){
    			paiList[0][i]--;
        	}
    		return normalHuPai.checkHu(paiList);
    	}
    	if(!result){
    	result = true;
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(result){//如果是在条里面有一条龙
    		for(int i=9;i<18;i++){
    			paiList[0][i]--;
        	}
    		return normalHuPai.checkHu(paiList);
    	}
    	}
    	if(!result){
    	result = true;
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(result){//如果是在筒里面有一条龙
    		for(int i=18;i<27;i++){
    			paiList[0][i]--;
        	}
    		return normalHuPai.checkHu(paiList);
    	}
    	}
    	return result;
    }
    
    private int checkQys(int[][] paiList){//清一色为1，混一色为2，没有返回0
    	int flag=-1;
    	int hunflag = -1;
    	int result = 1;//标识清一色
    	for(int i=0;i<paiList[0].length;i++){
    		int curflag = i/9;
    		if(paiList[0][i]>=1&&i<34){
    			if(flag==-1){
    				flag = curflag;
    				continue;
    			}else if(flag!=-1&&flag!=curflag){//不是清一色
    				if(i>26&&hunflag==-1){
    					hunflag = curflag;
    					result = 2;
    					continue;
    				}else if(i>26&&hunflag!=-1&&hunflag==curflag){
    					continue;
    				}else{
    					return 0;
    				}
    			}else{
    				continue;
    			}
    		}
    	}
    	if(result>0)
    	System.out.println("能胡清一色"+result);
    	return result;
    	
    }
    
    private final static int[] idxs = {0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};
	public boolean checkThirteen(int[][] paiList){
		boolean rv = true;
        int i = 0;

        while (rv && i < idxs.length) {
            rv = paiList[0][idxs[i++]] >= 1;
        }
		return rv;
	}

    /**
     * 检查是否七小对胡牌
     * @param paiList
     * @return 0-没有胡牌。1-普通七小对，2-龙七对
     */
    public int checkSevenDouble(int[][] paiList){
        int result = 1;
        
        	for(int i=0;i<paiList[0].length&&i<34;i++){
        		if(paiList[0][i] != 0){
        			if(paiList[0][i] != 2 && paiList[0][i] != 4){
        				return 0;
        			}else{
        				if(paiList[1][i] != 0){
        					return 0;
        				}else {
        					if (paiList[0][i] == 4) {
        						result = 2;
        					}
        				}
        			}
        		}
        	}
        	if(result>0)
        		System.out.println("能胡七对"+result);
        return result;
    }
    
    
//    private boolean checkSelfTing(int[][] paiList, boolean bclone) {
//		boolean rv = false;
//		for(int i = 0; i < 34; ++i) {
//			
//			if(checkHu(paiList,i)){
//				return true;
//			}
//			else{
//				continue;
//			}
//		}
//		return rv;
//	}
    
    private boolean checkSelfTing(int[][] paiList) {
		boolean rv = false;
		for(int i = 0; i < 34; ++i) {
			if (paiList[0][i] > 3) {
				continue;
			}
			paiList[0][i]++;
			rv = rv || checkSevenDouble(paiList) > 0;
			rv = rv || checkThirteen(paiList);
			rv = rv || normalHuPai.checkHu(paiList);

			paiList[0][i]--;

			if (rv) {
				break;
			}
		}
		return rv;
	}

	private boolean checkOtherTing(int[][] paiList, Integer cardIndex) {
		boolean rv = false;
		paiList[0][cardIndex]++;

		for(int i = 0; i < 34; ++i) {
			if (paiList[0][i] == 0 || i == cardIndex) {
				continue;
			}

			paiList[0][i]--;
			rv = rv || checkSelfTing(paiList);
			paiList[0][i]++;

			if (rv) {
				break;
			}
		}

		paiList[0][cardIndex]--;
		return rv;
	}

}
