# 네이버 자바
네이버 로그인 및 Java와 함께 쿠키 가져오기
## 예제
```java
package com.kiri.test;
import com.kiri.Naver;
public class Main {
	public static void main(String[] args) {
		try {
		    Naver naver = new Naver("네이버 아이디", "네이버 암호");
		    naver.login(false);
		    System.out.println(naver.getCookies());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```
## 매개변수
### 생성자
|매개변수|세부사항|자료형|필수의|
|----|----|----|----|
|```사용자 이름```|당신의 네이버 아이디|```끈```|Y|
|```암호```|당신의 네이버 암호|```끈```|Y|
### 로그인
|매개변수|세부사항|자료형|필수의|
|----|----|----|----|
|```n v 길다```|로그인을 유지하다|```불 방식의```|Y|
## 라이선스
네이버 자바는 [GPL 3.0](https://github.com/dev-kiri/Naver-java/blob/main/LICENSE) 라이선스를 따르고 있다.
