package swust.yang.util;

import java.util.HashMap;
import java.util.Map;

public class ResultsSummarOfLint {

	//key-检查项   value-错误数
	private static Map<String,Integer> map;
	
	private static boolean flag;
	
	static {
		map = new HashMap<String,Integer>();
		flag = false;
	}
	
	/**
	 * 
	 * @return 返回flag的值，flag为true表示此时处于批量执行阶段
	 */
	public static boolean getFlag() {
		return flag;
	}

	public static void setFlag(boolean flag) {
		ResultsSummarOfLint.flag = flag;
	}
	
	/**
	 * map(错误结果汇总集)数据清空
	 */
	public static void clear() {
		map.clear();
	}
	
	/**
	 * 
	 * @param errorType 错误类型
	 * @param errorNum 错误个数
	 */
	public static void upateErrorNum(String errorType,Integer errorNum) {
		if(map.containsKey(errorType)) {
			errorNum += map.get(errorType);
		}
		map.put(errorType, errorNum);
	}
	
	/**
	 *  
	 * @param resultMap 用于储存汇总的结果
	 * @return 汇总结果统计(错误类型-错误个数)
	 */
	public static void resultSum(Map<String,Integer> resultMap) {
		//处理当前汇总的数据
		for(String item : map.keySet()) {
			String key = CpplintDictionary.getChineseRule(item);
			Integer value = map.get(item);
			if(key == null) {
				resultMap.put("扩展规则错误", value);
			} else {
				resultMap.put(key, value);
			}
		}
	}
}
