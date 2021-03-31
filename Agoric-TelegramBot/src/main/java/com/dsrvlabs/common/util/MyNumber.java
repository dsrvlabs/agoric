package com.dsrvlabs.common.util;

import java.util.Random;

public class MyNumber {
	
	private static Random random = new Random();

	public static void main(String[] args) {
		MyNumber a = new MyNumber();
		a.getRandomInt(1000, 20);
	}
	
	public static void getRandom() {
		System.out.println( random.nextBoolean() );
		System.out.println( random.nextInt() );
		System.out.println( random.nextInt(5) );	// 0~4
		System.out.println( random.nextFloat() * 5 );	// 0.0000 ~ 4.9999
	}
	
	public static int getRandomInt(int base, int percent) {
		int gap = base * percent / 100;
		int min = base - gap;
		int max = base + gap;
		return random.nextInt(max - min) + min;
	}

}
