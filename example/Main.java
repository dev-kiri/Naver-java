package com.kiri.test;
import com.kiri.Naver;
public class Main {
	public static void main(String[] args) {
		try {
		    Naver naver = new Naver("NAVER ID", "NAVER PASSWORD");
		    naver.login(false);
		    System.out.println(naver.getCookies());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
