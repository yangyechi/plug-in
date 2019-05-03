package swust.yang.util;

import java.util.HashMap;
import java.util.Map;

public class CpplintDictionary {

	private static Map<String,String> cpplintMap;
	
	static {
		cpplintMap = new HashMap<String,String>();
		
		String[] cpplintEnRule = {
				"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl",
				"~ RULE_3_3_A_start_function_name_with_lowercase_unix",
				"~ RULE_6_1_E_do_not_use_more_than_5_paramters_in_function",
				"~ RULE_6_1_G_write_less_than_200_lines_for_function",
				"~ RULE_6_5_B_do_not_use_lowercase_for_macro_constants",
				"~ RULE_A_3_avoid_too_deep_block",
				"~ RULE_7_2_B_do_not_use_goto_statement",
				"~ RULE_4_4_A_do_not_write_over_120_columns_per_line",
				"~ RULE_4_1_A_A_use_tab_for_indentation",
				"~ RULE_4_1_A_B_use_space_for_indentation",
				"~ RULE_4_2_A_B_space_around_word",
				"~ RULE_4_5_B_use_braces_even_for_one_statement"			
		};
		String[] cpplintChRule = {
				"函数注释错误",
				"函数命名错误",
				"函数参数过多",
				"函数语句内部过长",
				"宏常量命名错误",
				"循环/选择嵌套深度大于3",
				"使用了goto语句",
				"每行代码长度过长",
				"缩进格式错误(not use tab)",
				"缩进格式错误(not use space)",
				"关键词(if/else/for)前后未使用空格",
				"关键词未使用大括号"
		}; 
		
		//cpplint字典初始化
		for(int i = 0; i < cpplintEnRule.length;i++) {
			cpplintMap.put(cpplintEnRule[i], cpplintChRule[i]);
		}
	}
	
	/**
	 * 
	 * @param enRule rule的英文表达
	 * @return rule的中文表达
	 */
	public static String getChineseRule(String enRule) {
		return cpplintMap.get(enRule);
	}
}
