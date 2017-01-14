package test.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.NormalHuPai;

public class HuAndScoreTest {

	
	private NormalHuPai normalHuPai = new NormalHuPai();
	public List<Avatar> playerList = new ArrayList<Avatar>();
	public static void main(String[] args) {
		HuAndScoreTest test = new HuAndScoreTest();
		// TODO Auto-generated method stub
		Avatar player1 = new Avatar();
		player1.canHu = true;
		test.playerList.add(player1);
		test.playerList.add(new Avatar());
		test.playerList.add(new Avatar());
		test.playerList.add(new Avatar());
		int[][] paiList = new int[2][42];
//		paiList[0]  = new int[]{0,0,0,3,0,0,1,1,1,    0,1,1,1,0,0,0,0,0,    0,0,2,0,0,0,1,1,1,   0,0,0,0,0,0,0,};
//		paiList[0]  = new int[]{0,0,2,2,2,2,2,2,2,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,  1,2,3,4,1,2,3,4};
		paiList[0]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    4,0,4,1,1,1,1,1,4,   0,0,0,0,0,0,0,  0,2,3,1,0,1,3,4};
//		paiList[0]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    3,1,1,1,1,2,1,1,3,   0,0,0,0,0,0,0,  1,2,3,4,1,2,3,4};
		paiList[1]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0};
//		paiList[1]  = new int[]{0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,    0,0,0,0,0,0,0,0,0,   0,0,0,0,0,0,0,};
    	Map<String,Integer> result = test.checkHu(paiList,player1,26);
//    	boolean resultting = test.checkSelfTing(paiList,true);
//    	System.out.println(resultting);
	}

	private Map<String,Integer> checkHu(int[][] paiList,Avatar avatar,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       Map<String,Integer> result = new HashMap<String,Integer>();
     //处理胡牌的逻辑
   			//可七小队
   			int isSeven = checkSevenDouble(paiList.clone());
               if(isSeven == 0){
                   //System.out.println("没有七小对");
            	   if(checkThirteen(paiList.clone())==1){
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
            	   if(checkThirteen(paiList.clone())==1){
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
            	   if(checkThirteen(paiList.clone())==1){
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
    		List mingang =avatar.avatarVO.getHuReturnObjectVO().getGangAndHuInfos().get("5");
    		if(mingang!=null&&mingang.size()>0){//有明杠
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
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int flag = cardIndex/9;
    	//先判断边,分为左边和右边
    	if(cardIndex-2>=flag*9&&pai[cardIndex-1]>0&&pai[cardIndex-2]>0){//右边
    		pai[cardIndex]-=1;
    		pai[cardIndex-1]-=1;
    		pai[cardIndex-2]-=1;
    		if(normalHuPai.isHuPai(pai)){
    			System.out.println("能胡右边");
    			return true;
    		}
    	}else if(cardIndex+2<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex+2]>0){//左边
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex+2]--;
    		if(normalHuPai.isHuPai(pai)){
    			System.out.println("能胡左边");
    			return true;
    		}
    	}
    	//然后判断坎
    	if(cardIndex-1>=flag*9&&cardIndex+1<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex-1]>0){
    		pai[cardIndex]-=1;
    		pai[cardIndex+1]-=1;
    		pai[cardIndex-1]-=1;
    		if(normalHuPai.isHuPai(pai)){
    			System.out.println("能胡坎");
    			return true;
    		}
    	}
    	//最后判断钓
    	if(pai[cardIndex]>=2){
    		pai[cardIndex]-=2;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			System.out.println("能胡钓");
    			return true;
    		}
    	}
    	
    	return result;
    }
    
    
    private boolean checkKan5(int[][] paiList){//判断视否坎五万
    	boolean result = false;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	if(pai[3] >= 1 && pai[5] >= 1){
    		pai[3]-=1;
    		pai[4]-=1;
    		pai[5]-=1;
    		if(normalHuPai.isHuPai(pai)){
    			System.out.println("能胡坎五万");
    			return true;
    		}
    	}
    	if(pai[4] >= 2){
    		pai[4] -=2 ;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			normalHuPai.setJIANG(0);
    			System.out.println("单吊坎五万");
    			return true;
    		}
    		normalHuPai.setJIANG(0);
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
    		if(i%9==8&&flag>=8){
    			return true;
    		}else{
    			flag = 0;
    			continue;
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
    	if(!result)
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}else{
    		return true;
    	}
    	if(!result)
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}else{
    		return true;
    	}
    	if(result)
    		System.out.println("能胡一条龙");
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
    
    public int checkThirteen(int[][] paiList){
    	int result = 1;
    	for(int i=0;i<paiList[0].length&&i<34;i++){
    		if(((i>26&&i<34)||(i%9==0||i%9==8))){
    			if(paiList[0][i]>= 1)
    			continue;
    			else{
    				result = 0;
    				break;
    			}
    		}else{
    			if(paiList[0][i]>= 1){
    			result = 0;
    			break;
    			}
    		}
    	}
    	if(result==1)
    		System.out.println("能胡十三幺");
    	return result;
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
    
    private boolean checkSelfTing(int[][] paiList, boolean bclone) {
		boolean rv = false;
		for(int i = 0; i < 34; ++i) {
			
			if(checkHu(paiList,i)){
				return true;
			}
			else{
				continue;
			}
		}
		return rv;
	}

}
