package swust.yang.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsg;
import swust.yang.service.IPlug;

@DisplayName("代码规范性检查")
class CpplintPlugTest {

	private IPlug cpplint;

	@BeforeEach
	void init() throws Exception {
		cpplint = new CpplintPlug();
	}

	@AfterEach
	void tearDown() throws Exception {
		cpplint = null;
	}

	@Test
	@DisplayName("singleExecute => 异常测试")
	void testSingleExecute() {
		assertAll(()->{
			
			//toolPath不存在
			assertNull(cpplint.singleExecute(null, "E:\\test1234", null, null));
			
			//toolPath存在但不是一个目录
			assertNull(cpplint.singleExecute(null, "E:\\test\\5120152516.cpp", null, null));
		
			//filePath不存在
			assertNull(cpplint.singleExecute(null, "E:\\test", "E:\\test1234", null));
			
			//filePath是一个目录
			assertNull(cpplint.singleExecute(null, "E:\\test", "E:\\test", null));
			
			//logDir不存在
			assertNull(cpplint.singleExecute(null, "E:\\test", 
						"E:\\test\\5120152516.cpp", "E:\\test111"));
			
			//logDir不是一个目录
			assertNull(cpplint.singleExecute(null, "E:\\test",
						"E:\\test\\5120152516.cpp", "E:\\test\\5120152516.cpp"));
		});
	}

	
	@Test
	@DisplayName("singleExecute => 基础规则测试")
	void testSingleExecute2() {
		String configInfo = "{\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10.888\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"55.55\"\r\n" + "}";
		String filePath = "E:\\test\\5120152516.cpp";
		String logDir = "E:\\log";
		String toolPath = "E:\\nsiqcppstyle";
		assertAll(() -> {
			ResultMsg ret = cpplint.singleExecute(configInfo, toolPath, filePath, logDir);
			assertEquals(63.0f, ret.getScore());
			assertEquals("5120152516", ret.getStudentInfor());
		});
	}

	@Test
	@DisplayName("singleExecute => 扩展规则测试")
	void testSingleExecute3() {
		long start = System.currentTimeMillis();
		String configInfo = "{\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"35\"\r\n" + "}";
		String filePath = "E:\\test\\ExtendTest.cpp";
		String logDir = "E:\\log";
		String toolPath = "E:\\nsiqcppstyle";
		assertAll(() -> {
			ResultMsg ret = cpplint.singleExecute(configInfo, toolPath, filePath, logDir);
			System.out.println("检查单个作业耗时：" + (System.currentTimeMillis() - start) + "ms");
			assertEquals(29.0f, ret.getScore());
			assertEquals("ExtendTest", ret.getStudentInfor());
		});
	}

	@Test
	@DisplayName("batchExecute => 异常测试")
	void testBatchExecute() {
		assertAll(() -> {
			//toolPath不存在
			assertNull(cpplint.batchExecute(null, "E:\\test123", "E:\\test", "E:\\log"));
			
			//toolPath不是一个目录
			assertNull(cpplint.batchExecute(null, "E:\\test\\5120152516.cpp", 
						"E:\\test", "E:\\log"));
			
			//srcDir不存在
			assertNull(cpplint.batchExecute(null, "E:\\test", "E:\\test11", "E:\\log"));
			
			//srcDir不是一个目录
			assertNull(cpplint.batchExecute(null, "E:\\test", 
						"E:\\test\\5120152516.cpp", "E:\\log"));
			
			//logDir不存在
			assertNull(cpplint.batchExecute(null, "E:\\test", "E:\\test", "E:\\log123"));
			
			//logDir不是一个目录
			assertNull(cpplint.batchExecute(null, "E:\\test", 
						"E:\\test", "E:\\log\\5120152516.log"));
		});
	}

	@Test
	@DisplayName("batchExecute => 基础规则测试")
	void testBatchExecute2() {
		String configInfo = "{\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"30\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"20\"\r\n" + "}";
		String filePath = "E:\\batchTest";
		String logDir = "E:\\batchLog";
		String toolPath = "E:\\nsiqcppstyle";
		assertAll(() -> {
			long start = System.currentTimeMillis();
			List<ResultMsg> list = cpplint.batchExecute(configInfo, toolPath, filePath, logDir);
			System.out.println("批量执行耗时：" + (System.currentTimeMillis() - start) + "ms");
			assertEquals(50.0f, list.get(0).getScore());
			assertEquals("5120151234", list.get(0).getStudentInfor());

			assertEquals(47.0f, list.get(1).getScore());
			assertEquals("5120152516", list.get(1).getStudentInfor());

			assertEquals(49.0, list.get(2).getScore());
			assertEquals("5120154444", list.get(2).getStudentInfor());

			assertEquals(47.0, list.get(3).getScore());
			assertEquals("ExtendTest", list.get(3).getStudentInfor());
		});
	}

	@Test
	@DisplayName("batchExecute => 扩展规则测试")
	void testBatchExecute3() {
		String configInfo = "{\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"20\"\r\n" + "}";
		String filePath = "E:\\batchTest";
		String logDir = "E:\\batchLog";
		String toolPath = "E:\\nsiqcppstyle";
		assertAll(() -> {
			long start = System.currentTimeMillis();
			List<ResultMsg> list = cpplint.batchExecute(configInfo, toolPath, filePath, logDir);
			System.out.println("批量执行耗时：" + (System.currentTimeMillis() - start) + "ms");
			assertEquals(20.0f, list.get(0).getScore());
			assertEquals("5120151234", list.get(0).getStudentInfor());

			assertEquals(17.0f, list.get(1).getScore());
			assertEquals("5120152516", list.get(1).getStudentInfor());

			assertEquals(19.0f, list.get(2).getScore());
			assertEquals("5120154444", list.get(2).getStudentInfor());

			assertEquals(16.0f, list.get(3).getScore());
			assertEquals("ExtendTest", list.get(3).getStudentInfor());
		});
	}

	@Test
	@DisplayName("checkConfigInfo => 总分测试")
	void testCheckConfigInfo() {
		String configInfo = null;
		String msg = null;
		configInfo = "{\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"20\"\r\n" + "}";
		// 测试没有设置总分
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分不能为空！", msg);

		// 测试总分为50且总分和各检查分数之和不相等
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"50\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"20\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分与各检查项分数之和不相等，请检查！", msg);
		
		// 测试总分为0
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"0\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"10\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"20\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且不能为特殊字符/字母！", msg);
		
		// 测试总分为100，且总分和各检查分数之和相等
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"20\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("OK", msg);

		//测试总分小于0
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"-1.1\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"20\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且小于等于100！", msg);
		
		//测试总分大于100
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"101.1\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"20\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且小于等于100！", msg);
		
		//测试总分为特殊符号
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"########\",\r\n" + 
				"	\"checkExtendRules\": \"~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n" + 
				"	\"scoreOfExtendRules\": \"@@##$$%%%$^&*\"\r\n" + 
				"}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且不能为特殊字符/字母！", msg);
	}
	
	@Test
	@DisplayName("checkConfigInfo => 基础规则测试")
	void testCheckConfigInfo2() {
		String configInfo = null;
		String msg = null;
		
		//勾选了基础规则,但没有设置对应分数
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
			//	+ "	\"scoreOfFuncAnnotation\": 20,\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("每一项检查项及其对应分数必须同时为空或者同时不为空！", msg);
		
		//没有勾选规则，但设置了对应分数
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				//+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"20\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("每一项检查项及其对应分数必须同时为空或者同时不为空！", msg);
		
		//设置了检查项和分数，但分数为0
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"0\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且不能为特殊字符/字母！", msg);
		
		//设置了检查项和分数，但分数为110
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n"
				+ "	\"scoreOfFuncAnnotation\": \"110\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"80\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且小于等于100！", msg);
		
		//设置了检查项和分数，但分数为字母
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n" + 
				"	\"scoreOfFuncAnnotation\": \"abcd\"\r\n" + 
				"}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且不能为特殊字符/字母！", msg);	
	}

	@Test
	@DisplayName("checkConfigInfo => 扩展规则测试")
	void testCheckConfigInfo3() {
		String configInfo = null;
		String msg = null;
		
		configInfo =  "{\r\n" 
				+ "	\"totalScore\": \"50\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"50\"\r\n" + "}";
		//扩展项规则和分数都设置,且分数与总分相等
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("OK", msg);	
		
		//设置了扩展项规则，但没有设置分数
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
			//	+ "	\"scoreOfExtendRules\": 100\r\n" + "}";
				+ "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项分数不能为空！", msg);	
		
		//设置了扩展项分数，但没有设置规则
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
			//	+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"100\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项规则不能为空！", msg);	
		
		//设置了扩展项分数和规则，但分数为0
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"0\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项分数必须大于0且不能为特殊字符/字母！", msg);	
		
		//设置了扩展项分数和规则，但分数为100且各检查项分数之和与总分不相等
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"50\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"100\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("总分与各检查项分数之和不相等，请检查！", msg);
		
		//设置了扩展项分数和规则，但分数小于0
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"-1.1\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项分数必须大于0且小于等于100！", msg);
		
		//设置了扩展项分数和规则，但分数大于100
		configInfo = "{\r\n" 
				+ "	\"totalScore\": \"100\",\r\n"
				+ "	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;~ RULE_4_2_A_B_space_around_word;\",\r\n"
				+ "	\"scoreOfExtendRules\": \"101.888\"\r\n" + "}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项分数必须大于0且小于等于100！", msg);
		
		//设置了扩展项分数和规则，但分数是特殊符号
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"checkExtendRules\": \"~ RULE_3_1_A_do_not_start_filename_with_underbar;~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n" + 
				"	\"scoreOfExtendRules\": \"@@##$$%%%$^&*\"\r\n" + 
				"}";
		msg = cpplint.checkConfigInfo(configInfo);
		assertEquals("扩展检查项分数必须大于0且不能为特殊字符/字母！", msg);
	}
	
	@DisplayName("getPluginInfo => 获取插件信息")
	@Test
	void testGetPluginInfo() {
		PluginInfo plugInfo = cpplint.getPluginInfo();
		assertEquals("cpplint-plug-1.0.jar",plugInfo.getName());
		assertEquals("yangyechi",plugInfo.getAuthor());
		assertEquals("Code specification check plug-in",plugInfo.getDescription());
		assertEquals("1.0",plugInfo.getVersion());
		assertEquals("swust.yang.service.impl.CpplintPlug",plugInfo.getClassName());
	}
	
	@DisplayName("getHtml => 第一次获取插件配置前端页面")
	@Test
	void testGetHtml() {
		String html = cpplint.getHtml(null);
		System.out.println(html);
	}

	@DisplayName("getHtml => 基础规则配置")
	@Test
	void testGetHtml2() {
		String	configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"checkFuncAnnotation\": \"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\",\r\n" + 
				"	\"scoreOfFuncAnnotation\":\"15\"\r\n" + 
				"}";
		String html = cpplint.getHtml(configInfo);
		System.out.println(html);
	}
	
	@DisplayName("getHtml => tab缩进选择测试")
	@Test
	void testGetHtml3() {
		String	configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"checkIdentationStyle\": \"~ RULE_4_1_A_A_use_tab_for_indentation\",\r\n" + 
				"	\"scoreOfIdentationStyle\":\"15\"\r\n" + 
				"}";
		String html = cpplint.getHtml(configInfo);
		System.out.println(html);
	}
	
	@DisplayName("getHtml => space缩进选择测试")
	@Test
	void testGetHtml4() {
		String	configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"checkIdentationStyle\": \"~ RULE_4_1_A_B_use_space_for_indentation\",\r\n" + 
				"	\"scoreOfIdentationStyle\":\"15\"\r\n" + 
				"}";
		String html = cpplint.getHtml(configInfo);
		System.out.println(html);
	}
	
	@DisplayName("getHtml => 扩展规则测试")
	@Test
	void testGetHtml5() {
		String configInfo =  "{\r\n" + 
				"	\"totalScore\": \"50\",\r\n" + 
				"	\"checkExtendRules\": \"~ RULE_3_3_A_start_function_name_with_is_or_has_when_return_bool;\\n\\r\\n~ RULE_3_2_B_do_not_use_same_filename_more_than_once;\",\r\n" + 
				"	\"scoreOfExtendRules\": \"50\"\r\n" + 
				"}";
		String html = cpplint.getHtml(configInfo);
		System.out.println(html);
	}
}
