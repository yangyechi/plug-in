package swust.yang.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import swust.yang.entity.CpplintConfigInfo;

@DisplayName("处理Json字符串")
class JSonToObjectTest {

	@Test
	@DisplayName("JSon字符串转对象")
	void testJSonStrToObject() {
		String Json = "{\r\n" + 
				"	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n" + 
				"	\"scoreOfFuncAnnotation\": 10.888,\r\n" + 
				"	\"checkExtendRules\": \"~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n" + 
				"	\"scoreOfExtendRules\": 55.55\r\n" + 
				"}";
		assertAll(()->{
			CpplintConfigInfo config = JSonToObject.JSonStrToObject(Json);
			assertEquals("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl",
						config.getCheckFuncAnnotation());
			assertEquals(10.888f,config.getScoreOfFuncAnnotation().floatValue());
		
			assertEquals("~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;",
						config.getCheckExtendRules());
			assertEquals(55.55f,config.getScoreOfExtendRules().floatValue());
			
		});
	}

	@Test
	@DisplayName("JSon字符串转对象空格/换行")
	void testJSonStrToObject2() {
		String Json =  "{\r\n" + 
				"	\"totalScore\": \"50\",\r\n" + 
				"	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;   \\n\\r\\n~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n" + 
				"	\"scoreOfExtendRules\": \"50\"\r\n" + 
				"}";
		CpplintConfigInfo config = JSonToObject.JSonStrToObject(Json);
		String rules = config.getCheckExtendRules();
		String[] arr = rules.split(";");
		assertEquals("~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool",arr[0].trim());
		assertEquals("~ RULE_3_2_B_do_not_use_same_filename_more_than_once",arr[1].trim());
		for(String item : arr) {
			System.out.println(item.trim());
		}
		System.out.println(arr.length);
	}
}
