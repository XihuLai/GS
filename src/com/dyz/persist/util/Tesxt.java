package com.dyz.persist.util;

public class Tesxt {
	public static void main(String[] args) {
		
		System.out.println(returnMa(27));
		
		
	}
	public static int returnMa(int cardPoint){
		if(cardPoint  <= 8){ 
			return cardPoint;
		}
		else {
			cardPoint = cardPoint-9;
			return returnMa(cardPoint);
		}
	}
}
