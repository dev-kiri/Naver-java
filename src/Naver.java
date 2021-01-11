package com.kiri;

import com.lzstring.LZString;
import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.RSAPublicKeySpec;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.Cipher;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject; 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Connection;

public class Naver {
    protected String username;
    protected String password;
    private String uuid;
    private HashMap<String, String> cookies;
    private String userAgent;
    private static int i; 

    public Naver(String username, String password) {
        this.username = username;
        this.password = password;
        this.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";
        this.cookies = new HashMap<>();
    }

    private static String getLenChar(String value) {
        char c = (char) value.length();
        return Character.toString(c);
    }

    private static String encrypt(String n, String e, String m) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        BigInteger nvalue = new BigInteger(n, 16);
        BigInteger evalue = new BigInteger(e, 16);
        RSAPublicKeySpec pks = new RSAPublicKeySpec(nvalue, evalue);
        PublicKey key = keyFactory.generatePublic(pks);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipher.update(m.getBytes());
        byte[] hash = cipher.doFinal();
        BigInteger hex = new BigInteger(1, hash);
        return String.format("%040x", hex);
    }

    private static JSONObject bvsdformat(String uuid, String username, String userAgent) throws JSONException {
        JSONObject d1 = new JSONObject("{}")
                .put("i", "id")
                .put("b", new JSONObject("{}").put("a", new JSONArray("[]").put("0," + username)))
                .put("d", username)
                .put("e", false)
                .put("f", false);
        JSONObject d2 = new JSONObject("{}")
                .put("i", "pw")
                .put("e", true)
                .put("f", false);
        return new JSONObject("{}")
                .put("a", uuid + "-4")
                .put("b", "1.3.4")
                .put("d", new JSONArray("[]").put(d1).put(d2))
                .put("h", "1f")
                .put("i", new JSONObject("{}").put("a", userAgent));
    }

    private static String[] generateKeys() throws IOException {
        String keys = Jsoup.connect("https://nid.naver.com/login/ext/keys2.nhn").get().text();
        return keys.split(",");
    }

    public void login(Boolean nvlong) throws Exception {
        this.uuid = UUID.randomUUID().toString();
        String[] keys = Naver.generateKeys();
        String sessionkey = keys[0];
        String keyname = keys[1];
        String nvalue = keys[2];
        String evalue = keys[3];
        String message = Naver.getLenChar(sessionkey) + sessionkey +
                         Naver.getLenChar(this.username) + this.username +
                         Naver.getLenChar(this.password) + this.password;
        String encpw = Naver.encrypt(nvalue, evalue, message);
        String bvsd = Naver.bvsdformat(this.uuid, this.username, this.userAgent).toString();
        String encData = LZString.compressToEncodedURIComponent(bvsd);
        String final_bvsd = new JSONObject("{}").put("uuid", this.uuid).put("encData", encData).toString();
        Connection.Response response = Jsoup.connect("https://nid.naver.com/nidlogin.login")
                .userAgent(this.userAgent)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data("encpw", encpw)
                .data("enctp", "1")
                .data("svctype", "1")
                .data("smart_LEVEL", "-1")
                .data("bvsd", final_bvsd)
                .data("encnm", keyname)
                .data("locale", "ko_KR")
                .data("url", "https://naver.com")
                .data("nvlong", nvlong ? "on" : "off")
                .execute();
        Document doc = response.parse();
        Elements token = doc.select("input#token_push");
        if (!token.isEmpty()) {
        	System.out.println("2-step verification notification has been sent. Please check the notification on your NAVER App.");
        	i = 10;
        	Timer timer = new Timer();
        	TimerTask loginTask = new TimerTask() {
        		@Override
        		public void run() {
        			if (i == 0) {
        				System.out.println("Time Exceeded!");
        				timer.cancel();
        				return;
        			} else {
        				System.out.println(i + " minutes remaining...");
        				i--;
        			}
        		}
        	};
        	timer.scheduleAtFixedRate(loginTask, 0, 60000);
            String key = doc.select("input#key").first().attr("value");
            String session = token.first().attr("value");
            Map<String, String> cookies = Jsoup.connect("https://nid.naver.com/push/otp?session=" + session).execute().cookies();
            Connection.Response auth = Jsoup.connect("https://nid.naver.com/nidlogin.login")
                    .userAgent(this.userAgent)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .cookies(cookies)
                    .data("mode", "otp")
                    .data("auto", "")
                    .data("token_push", session)
                    .data("locale", "ko_KR")
                    .data("key", key)
                    .data("otp", "")
                    .execute();
            if (!auth.hasCookie("NID_AUT")) throw new IllegalArgumentException("Invalid ID or PASSWORD.");
            this.cookies.put("nid_inf", auth.cookie("nid_inf"));
            this.cookies.put("NID_AUT", auth.cookie("NID_AUT"));
            this.cookies.put("NID_JKL", auth.cookie("NID_JKL"));
            this.cookies.put("NID_SES", auth.cookie("NID_SES"));
            timer.cancel();
        } else {
            if (!response.hasCookie("NID_AUT")) throw new IllegalArgumentException("Invalid ID or PASSWORD.");
            this.cookies.put("nid_inf", response.cookie("nid_inf"));
            this.cookies.put("NID_AUT", response.cookie("NID_AUT"));
            this.cookies.put("NID_JKL", response.cookie("NID_JKL"));
            this.cookies.put("NID_SES", response.cookie("NID_SES"));
        }
    }
    
    public HashMap<String, String> getCookies() {
    	return this.cookies;
    }
}
