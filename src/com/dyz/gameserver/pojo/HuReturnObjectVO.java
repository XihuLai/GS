package com.dyz.gameserver.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.proxy.utils.StringUtilities;

import com.dyz.persist.util.StringUtil;

/**
 * 胡牌时返回信息组成对象
 * 牌组，杠的详细信息，胡的详细信息，昵称，uuid
 * @author luck
 *
 */
public class HuReturnObjectVO {

	/**
     * 牌数组
     * 
     */
    private int[]paiArray;
    /**
     * key:type:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
     * value:list里面，第一个为点炮/杠/胡次数，第二个元素为点炮/杠/胡分数总和
     */
    private Map<String , ArrayList<Integer>> gangAndHuInfos; 
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 玩家uuid
     */
    private Integer uuid;
    /**
     * 杠的总分
     */
    private int gangScore;
    /**
     * 总分
     */
    private int totalScore;
    /**
     * 存放吃，碰，杠，胡的信息
     * 
     */
    private Map<String , String> totalInfo;
    
    
    
	public Map<String, String> getTotalInfo() {
		return totalInfo;
	}
	/**
	 * 统计吃，碰，杠，胡的详细信息， 比如 谁杠了谁的什么牌，明杠，还是暗杠等
	 * @param type 信息类型  "chi","peng","gang","hu",
	 * @param str   信息内容
	 */
	public synchronized void updateTotalInfo(String type ,String str) {
		if(type.equals("chi")){
			System.out.println("chi");
		}
		if(StringUtil.isNotEmpty(str)){
			if(totalInfo == null){
				this.totalInfo = new HashMap<String, String>();
			}
			if(totalInfo.get(type) == null){
				totalInfo.put(type, str);
			}
			else{
				totalInfo.put(type, totalInfo.get(type)+","+str);
			}
			
		}
		else{
			System.out.println("HuReturnObjectVO里面的updateTotalInfo--传入的str不呢为空");
		}
	}
	
	/**
	 * 统计 不同type的次数和(胡才有)得分
	 *  游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(type),
	 * @param type 类型 
	 * @param score 分数
	 */
	public synchronized void updateGangAndHuInfos(String type , int score) {
		ArrayList<Integer> listNew = new ArrayList<Integer>();
		if(gangAndHuInfos == null){
			this.gangAndHuInfos = new HashMap<String, ArrayList<Integer>>();
			listNew.add(1);
			listNew.add(score);
		}
		else{
			ArrayList<Integer> list = gangAndHuInfos.get(type);
			if(list == null){
				listNew.add(1);
				listNew.add(score);
			}
			else{
				//在原来的基础上修改信息
				list = gangAndHuInfos.get(type);
				listNew.add(list.get(0)+1);
				listNew.add(list.get(1)+score);
			}
		}
		if(type.equals("4") || type.equals("5")){
			//杠的总分
			updateGangScore(score);
		}else{
			//胡之后游戏总分
			updateTotalScore(score);
		}
		this.gangAndHuInfos.put(type, listNew);
	}
	public int[]getPaiArray() {
		return paiArray;
	}
	public void setPaiArray(int[] paiArray) {
		this.paiArray = paiArray;
	}
    public Map<String, ArrayList<Integer>> getGangAndHuInfos() {
		return gangAndHuInfos;
	}
	
	public int getGangScore() {
		return gangScore;
	}
	private void updateGangScore(int score) {
		if(gangScore == 0){
			this.gangScore = score;
		}
		else{
			this.gangScore = this.gangScore +score;
		}
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getUuid() {
		return uuid;
	}
	public void setUuid(Integer uuid) {
		this.uuid = uuid;
	}
	public int getTotalScore() {
		return totalScore;
	}
	//更新总分数
	private void updateTotalScore(int score) {
		if(totalScore == 0){
			this.totalScore = score;
		}
		else{
			this.totalScore = this.totalScore +score;
		}
	}
	
	
}
