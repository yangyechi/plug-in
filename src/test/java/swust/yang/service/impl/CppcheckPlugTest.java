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

class CppcheckPlugTest {
	private IPlug cppcheck;

	@BeforeEach
	void init() throws Exception {
		cppcheck = new CppcheckPlug();
	}

	@AfterEach
	void tearDown() throws Exception {
		cppcheck = null;
	}

	@Test
	@DisplayName("singleExecute => 异常测试")
	void testSingleExecute() {
		assertAll(()->{
			
			//toolPath不存在
			assertNull(cppcheck.singleExecute(null, "E:\\test1234", null, null));
			
			//toolPath存在但不是一个目录
			assertNull(cppcheck.singleExecute(null, "E:\\test\\5120152516.cpp", null, null));
		
			//filePath不存在
			assertNull(cppcheck.singleExecute(null, "E:\\test", "E:\\test1234", null));
			
			//filePath是一个目录
			assertNull(cppcheck.singleExecute(null, "E:\\test", "E:\\test", null));
			
			//logDir不存在
			assertNull(cppcheck.singleExecute(null, "E:\\test", 
						"E:\\test\\5120152516.cpp", "E:\\test111"));
			
			//logDir不是一个目录
			assertNull(cppcheck.singleExecute(null, "E:\\test",
						"E:\\test\\5120152516.cpp", "E:\\test\\5120152516.cpp"));
			
			//传入的待检查作业不是c/cpp文件
			assertNull(cppcheck.singleExecute(null, "E:\\test",
						"E:\\test\\5120152516.app", "E:\\log"));
		});
	}

	@Test
	@DisplayName("singleExecute => 默认检查项测试(error)")
	void testSingleExecute2() {
		String configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"scoreOfError\": \"50\"\r\n" + 
				"}";
		String filePath = "E:\\cppcheckTest\\5120152516.c";
		String logDir = "E:\\cppcheckLog";
		String toolPath = "E:\\cppcheck";
		assertAll(() -> {
			ResultMsg ret = cppcheck.singleExecute(configInfo, toolPath, filePath, logDir);
			assertEquals("45.0", ret.getValue());
			assertEquals("5120152516", ret.getStudentInfor());
		});
	}
	
	@Test
	@DisplayName("singleExecute => 扩展检查项测试")
	void testSingleExecute3() {
		String configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"5\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		String filePath = "E:\\cppcheckTest\\5120151234.cpp";
		String logDir = "E:\\cppcheckLog";
		String toolPath = "E:\\cppcheck";
		assertAll(() -> {
			ResultMsg ret = cppcheck.singleExecute(configInfo, toolPath, filePath, logDir);
			assertEquals("64.0", ret.getValue());
			assertEquals("5120151234", ret.getStudentInfor());
		});
	}
	
	@Test
	@DisplayName("batchExecute => 异常测试")
	void testBatchExecute() {
		assertAll(() -> {
			//toolPath不存在
			assertNull(cppcheck.batchExecute(null, "E:\\test123", "E:\\test", "E:\\log"));
			
			//toolPath不是一个目录
			assertNull(cppcheck.batchExecute(null, "E:\\test\\5120152516.cpp", 
						"E:\\test", "E:\\log"));
			
			//srcDir不存在
			assertNull(cppcheck.batchExecute(null, "E:\\test", "E:\\test11", "E:\\log"));
			
			//srcDir不是一个目录
			assertNull(cppcheck.batchExecute(null, "E:\\test", 
						"E:\\test\\5120152516.cpp", "E:\\log"));
			
			//logDir不存在
			assertNull(cppcheck.batchExecute(null, "E:\\test", "E:\\test", "E:\\log123"));
			
			//logDir不是一个目录
			assertNull(cppcheck.batchExecute(null, "E:\\test", 
						"E:\\test", "E:\\log\\5120152516.log"));
		});
	}

	@Test
	@DisplayName("batchExecute => 批量测试")
	void testBatchExecute2() {
		String configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkStyle\": \"style\",\r\n" + 
				"	\"scoreOfStyle\": \"5\",\r\n" + 
				"	\"checkPerformance\": \"performance\",\r\n" + 
				"	\"scoreOfPerformance\": \"10\"\r\n" + 
				"}";
		String filePath = "E:\\cppcheckTest";
		String logDir = "E:\\cppcheckLog";
		String toolPath = "E:\\cppcheck";
		assertAll(() -> {
			long start = System.currentTimeMillis();
			List<ResultMsg> list = cppcheck.batchExecute(configInfo, toolPath, filePath, logDir);
			System.out.println("cppcheck批量执行耗时：" + (System.currentTimeMillis() - start) + "ms");
			assertEquals("60.0", list.get(0).getValue());
			assertEquals("5120151234", list.get(0).getStudentInfor());

			assertEquals("55.0", list.get(1).getValue());
			assertEquals("5120152516", list.get(1).getStudentInfor());

			assertEquals("64.0", list.get(2).getValue());
			assertEquals("5120152544", list.get(2).getStudentInfor());
		});
	}
	
	@Test
	@DisplayName("checkConfigInfo => 总分测试")
	void testCheckConfigInfo() {
		String configInfo = null;
		String msg = null;
		
		// 测试没有设置总分
		configInfo = "\r\n" + 
				"{\r\n" + 
				"	\"scoreOfError\": \"50\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分不能为空！", msg);
		
		// 测试总分为50且总分和各检查分数之和不相等
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"50\",\r\n" + 
				"	\"scoreOfError\": \"20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分与各检查项分数之和不相等，请检查！", msg);
		
		// 测试总分为0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"0\",\r\n" + 
				"	\"scoreOfError\": \"20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且不能为特殊字符/字母！", msg);
		
		// 测试总分为100，且总分和各检查分数之和相等
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"20\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"30\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("OK", msg);
		
		//测试总分小于0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"-2\",\r\n" + 
				"	\"scoreOfError\": \"20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且小于等于100！", msg);
		
		//测试总分大于100
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"101\",\r\n" + 
				"	\"scoreOfError\": \"20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且小于等于100！", msg);
		
		//测试总分为特殊符号
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"###@#@#@#@\",\r\n" + 
				"	\"scoreOfError\": \"20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("总分必须大于0且不能为特殊字符/字母！", msg);
	}

	@Test
	@DisplayName("checkConfigInfo => 错误消息检查项分数设置测试")
	void testCheckConfigInfo2() {
		String configInfo = null;
		String msg = null;
		
		// 测试没有设置错误项分数
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("错误消息检查项分数不能为空！", msg);
		
		// 测试错误项分数为0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"10\",\r\n" + 
				"	\"scoreOfError\": \"0\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("错误消息检查项分数必须大于0且不能为特殊字符/字母！", msg);
		
		// 测试错误项分数为100且和总分相等
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"100\",\r\n" + 
				"	\"scoreOfError\": \"100\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("OK", msg);
		
		// 测试错误项分数小于0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"20\",\r\n" + 
				"	\"scoreOfError\": \"-20\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("错误消息检查项分数必须大于0且小于等于100！", msg);
		
		// 测试错误项分数大于100
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"20\",\r\n" + 
				"	\"scoreOfError\": \"100.1\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("错误消息检查项分数必须大于0且小于等于100！", msg);
		
		//测试错误项分数为特殊符号
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"20\",\r\n" + 
				"	\"scoreOfError\": \"！@#￥%%……\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("错误消息检查项分数必须大于0且不能为特殊字符/字母！", msg);
	}
	
	@Test
	@DisplayName("checkConfigInfo => 基础规则测试")
	void testCheckConfigInfo3() {
		String configInfo = null;
		String msg = null;
		
		//勾选了基础规则,但没有设置对应分数
		configInfo = "\r\n" + 
				"{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("每一项检查项及其对应分数必须同时为空或者同时不为空！", msg);
		
		//没有勾选规则，但设置了对应分数
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"scoreOfWarning\": \"5\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("每一项检查项及其对应分数必须同时为空或者同时不为空！", msg);
		
		//设置了检查项和分数，但分数为0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"0\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且不能为特殊字符/字母！", msg);
		
		//设置了检查项和分数，但分数为110
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"110\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且小于等于100！", msg);
		
		//设置了检查项和分数，但分数小于0
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"-110\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且小于等于100！", msg);
		
		//设置了检查项和分数，但分数为字母
		configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"！@#@￥#@%…………\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		msg = cppcheck.checkConfigInfo(configInfo);
		assertEquals("检查项分数必须大于0且不能为特殊字符/字母！", msg);	
	}

	
	@Test
	void testGetPluginInfo() {
		PluginInfo plugInfo = cppcheck.getPluginInfo();
		assertEquals("cppcheck-plug-1.0.jar",plugInfo.getName());
		assertEquals("yangyechi",plugInfo.getAuthor());
		assertEquals("Code static check plug-in",plugInfo.getDescription());
		assertEquals("1.0",plugInfo.getVersion());
		assertEquals("swust.yang.service.impl.CppcheckPlug",plugInfo.getClassName());
	}

	@Test
	@DisplayName("getHtml => 第一次获取插件配置前端页面")
	void testGetHtml() {
		String html = cppcheck.getHtml(null);
		System.out.println(html);
	}

	@Test
	@DisplayName("getHtml => 基础规则配置")
	void testGetHtml2() {
		String configInfo = "{\r\n" + 
				"	\"totalScore\": \"65\",\r\n" + 
				"	\"scoreOfError\": \"50\",\r\n" + 
				"	\"checkWarning\": \"warning\",\r\n" + 
				"	\"scoreOfWarning\": \"5\",\r\n" + 
				"	\"checkPortability\": \"portability\",\r\n" + 
				"	\"scoreOfPortability\": \"10\"\r\n" + 
				"}";
		String html = cppcheck.getHtml(configInfo);
		System.out.println(html);
	}

}
