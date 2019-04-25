package swust.yang.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dos命令")
class RunTaskTest {

	@Test
	@DisplayName("runCommand => nsiqcppstyle命令")
	void testRunCommand() {
		String executeCommand = "python E:\\nsiqcppstyle\\nsiqcppstyle.py --output=csv -o E:\\log\\5120152516.log -f E:\\test\\filefilter.txt E:\\test\\5120152516.cpp";
		assertAll(()->{
			RunTask.runCommand(executeCommand);
			File file = new File("E:\\log\\5120152516.log");			
			assertTrue(file.exists());
		});	
	}

	@Test
	@DisplayName("testGetTaskScore => 解析日志得出分数")
	void testGetTaskScoreOfLint() {
		Map<String, Float> map = new HashMap<String, Float>();
		map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 55.88f);
		assertAll(()->{
			float score = 0.0f;
			//得分不为0
			score = RunTask.getTaskScoreOfLint("E:\\log\\5120152516.log", map);
			assertEquals(53.0f,score);
			
			//得分为0
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 1.0f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\5120152516.log", map);
			assertEquals(0.0f,score);
			
			//没有出错的规则，分数累加
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 4.5f);
			map.put("~ RULE_4_5_B_use_braces_even_for_one_statement", 8.5f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\5120152516.log", map);
			assertEquals(10.0f,score);
			
			assertThrows(FileNotFoundException.class, () -> {
				 RunTask.getTaskScoreOfLint("E:\\log\\5120152518.log", map);
	        });
		});
	}
	@Test
	@DisplayName("testGetTaskScore => 解析没有错误的日志")
	void testGetTaskScoreOfLint2() {
		assertAll(()->{
			Map<String, Float> map = new HashMap<String, Float>();
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 55.88f);
			float score = RunTask.getTaskScoreOfLint("E:\\log\\5120151234.log", map);
			assertEquals(56.0f,score);
			
			BufferedReader read = new BufferedReader(new FileReader("E:\\log\\5120151234.log"));
			assertEquals("Great! Check it out!",read.readLine());
			read.close();
		});
	}
	
	@Test
	@DisplayName("testGetTaskScore => 解析日志得出分数-cppcheck")
	void testGetTaskScoreOfCheck() {
		assertAll(()->{
			float score = 0.0f;
			//得分不为0
			Map<String, Float> map = new HashMap<String, Float>();
			map.put("error", 15.3f);
			map.put("style", 10.1f);
			map.put("warning", 3.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\someError.log", map);
			assertEquals(18.0f,score);

			//得分为0
			map.put("error", 4.3f);
			map.put("style", 3.1f);
			map.put("warning", 1.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\someError.log", map);
			assertEquals(0.0f,score);
			
			//设置了检查项但没有错误，成绩累加
			map.put("error", 15.4f);
			map.put("style", 10.1f);
			map.put("warning", 3.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\noErrorTest.log", map);
			assertEquals(29.0f,score);
			
			BufferedReader read = new BufferedReader(new FileReader("E:\\cppcheckLog\\noErrorTest.log"));
			assertEquals("Great! Check it out!",read.readLine());
			BufferedWriter write = new BufferedWriter(new FileWriter("E:\\cppcheckLog\\noErrorTest.log"));
			write.write("");
			read.close();
			write.close();
		});
	}
}
