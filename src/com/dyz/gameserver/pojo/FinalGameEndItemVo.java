package com.dyz.gameserver.pojo;


/**
 * 当房间次数用完之后放回整个房间，。胡 杠，点炮，接炮，次数和总分数
 * @author luck
 *
 */
public class FinalGameEndItemVo {

	private int uuid;
	private int zimo;
	private int jiepao;
	private int dianpao;
	private int minggang;
	private int angang;
	private int scores;
	public int getUuid() {
		return uuid;
	}
	public void setUuid(int uuid) {
		this.uuid = uuid;
	}
	public int getZimo() {
		return zimo;
	}
	public void setZimo(int zimo) {
		this.zimo = zimo;
	}
	public int getJiepao() {
		return jiepao;
	}
	public void setJiepao(int jiepao) {
		this.jiepao = jiepao;
	}
	public int getDianpao() {
		return dianpao;
	}
	public void setDianpao(int dianpao) {
		this.dianpao = dianpao;
	}
	public int getMinggang() {
		return minggang;
	}
	public void setMinggang(int minggang) {
		this.minggang = minggang;
	}
	public int getAngang() {
		return angang;
	}
	public void setAngang(int angang) {
		this.angang = angang;
	}
	public int getScores() {
		return scores;
	}
	public void setScores(int scores) {
		this.scores = scores;
	}

	
	
}
