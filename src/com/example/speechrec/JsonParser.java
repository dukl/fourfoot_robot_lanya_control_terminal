package com.example.speechrec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Json缁撴灉瑙ｆ瀽绫�
 */
public class JsonParser {

	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 杞啓缁撴灉璇嶏紝榛樿浣跨敤绗竴涓粨鏋�
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
//				濡傛灉闇�瑕佸鍊欓�夌粨鏋滐紝瑙ｆ瀽鏁扮粍鍏朵粬瀛楁
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
	
	public static String parseGrammarResult(String json, String engType) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			// 浜戠鍜屾湰鍦扮粨鏋滃垎鎯呭喌瑙ｆ瀽
			if ("cloud".equals(engType)) {
				for (int i = 0; i < words.length(); i++) {
					JSONArray items = words.getJSONObject(i).getJSONArray("cw");
					for(int j = 0; j < items.length(); j++)
					{
						JSONObject obj = items.getJSONObject(j);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("娌℃湁鍖归厤缁撴灉.");
							return ret.toString();
						}
						ret.append("銆愮粨鏋溿��" + obj.getString("w"));
						ret.append("銆愮疆淇″害銆�" + obj.getInt("sc"));
						ret.append("\n");
					}
				}
			} else if ("local".equals(engType)) {
				ret.append("銆愮粨鏋溿��");
				for (int i = 0; i < words.length(); i++) {
					JSONObject wsItem = words.getJSONObject(i);
					JSONArray items = wsItem.getJSONArray("cw");
					if ("<contact>".equals(wsItem.getString("slot"))) {
						// 鍙兘浼氭湁澶氫釜鑱旂郴浜轰緵閫夋嫨锛岀敤涓嫭鍙锋嫭璧锋潵锛岃繖浜涘�欓�夐」鍏锋湁鐩稿悓鐨勭疆淇″害
						ret.append("銆�");
						for(int j = 0; j < items.length(); j++)
						{
							JSONObject obj = items.getJSONObject(j);
							if(obj.getString("w").contains("nomatch"))
							{
								ret.append("娌℃湁鍖归厤缁撴灉.");
								return ret.toString();
							}
							ret.append(obj.getString("w")).append("|");						
						}
						ret.setCharAt(ret.length() - 1, 'z');
					} else {
						//鏈湴澶氬�欓�夋寜鐓х疆淇″害楂樹綆鎺掑簭锛屼竴鑸�夊彇绗竴涓粨鏋滃嵆鍙�
						JSONObject obj = items.getJSONObject(0);
						if(obj.getString("w").contains("nomatch"))
						{
							ret.append("娌℃湁鍖归厤缁撴灉.");
							return ret.toString();
						}
						ret.append(obj.getString("w"));						
					}
				}
				ret.append("銆愮疆淇″害銆�" + joResult.getInt("sc"));
				ret.append("\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("娌℃湁鍖归厤缁撴灉.");
		} 
		return ret.toString();
	}
	
	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("娌℃湁鍖归厤缁撴灉.");
						return ret.toString();
					}
					ret.append("銆愮粨鏋溿��" + obj.getString("w"));
					ret.append("銆愮疆淇″害銆�" + obj.getInt("sc"));
					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("娌℃湁鍖归厤缁撴灉.");
		} 
		return ret.toString();
	}

	public static String parseTransResult(String json,String key) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			String errorCode = joResult.optString("ret");
			if(!errorCode.equals("0")) {
				return joResult.optString("errmsg");
			}
			JSONObject transResult = joResult.optJSONObject("trans_result");
			ret.append(transResult.optString(key));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}
}
