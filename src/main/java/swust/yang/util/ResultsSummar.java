package swust.yang.util;

import java.util.HashMap;
import java.util.Map;

public class ResultsSummar {

	//key-检查项   value-错误数
	private static Map<String,Integer> map;
	
	public static void init() {
		map = new HashMap<String,Integer>();
	}
	
	public static void clear() {
		map.clear();
	}
	
	public static void put(String errorType,Integer errorNum) {
		if(map.containsKey(errorType)) {
			errorNum += map.get(errorType);
		}
		map.put(errorType, errorNum);
	}
	
	public static Map<String, Integer> getMap() {
		return map;
	}
}
