# Naver-java
Naver login and getting cookies with java
## Example
```java
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
```
## Parameter
### constructor
|Parameter|Detail|Type|Required|
|----|----|----|----|
|```username```|Naver ID|```String```|Y|
|```password```|Naver PASSWORD|```String```|Y|
### login
|Parameter|Detail|Type|Required|
|----|----|----|----|
|```nvlong```|Login continue|```Boolean```|Y|
## License
Naver-java is following [GPL 3.0](https://github.com/dev-kiri/Naver-java/blob/main/LICENSE) License.

