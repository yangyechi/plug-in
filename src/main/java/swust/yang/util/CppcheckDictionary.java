package swust.yang.util;

import java.util.HashMap;
import java.util.Map;

public class CppcheckDictionary {
	
	private static Map<String,String> cppcheckMap;
	
	static {
		cppcheckMap = new HashMap<String,String>();
		
		String[] cppcheckEnRule = {
				"error",
				"warning",
				"style",
				"portability",
				"performance"
		};
		
		String[] cppcheckChRule = {
				"错误消息",
				"警告消息",
				"风格警告消息",
				"可移植性警告消息",
				"性能警告消息"
		};
		
		//cppcheck字典初始化
		for(int i = 0; i < cppcheckEnRule.length;i++) {
			cppcheckMap.put(cppcheckEnRule[i], cppcheckChRule[i]);
		}
	}
	
	/**
	 * 
	 * @param enRule rule的英文表达
	 * @return rule的中文表达
	 */
	public static String getChineseRule(String enRule) {
		return cppcheckMap.get(enRule);
	}
}
