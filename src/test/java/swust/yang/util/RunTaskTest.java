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
	@DisplayName("testGetTaskScore => 解析日志得出分数-cpplint")
	void testGetTaskScoreOfLint() {
		Map<String, Float> map = new HashMap<String, Float>();
		assertAll(()->{
			float score = 0.0f;
			//得分不为0
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 55.0f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\runTaskTest1.log", map);
			assertEquals(50.0f,score);
			
			//错误个数比设置的分数大，得分为0
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 4.0f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\runTaskTest1.log", map);
			assertEquals(0.0f,score);
			
			//错误个数大于5，该类检查项得分置为0 
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 100.0f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\runTaskTest2.log", map);
			assertEquals(0.0f,score);
			
			//没有出错的规则，分数累加
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 4.0f);
			map.put("~ RULE_4_5_B_use_braces_even_for_one_statement", 8.0f);
			score = RunTask.getTaskScoreOfLint("E:\\log\\runTaskTest1.log", map);
			assertEquals(8.0f,score);
			
			assertThrows(FileNotFoundException.class, () -> {
				 RunTask.getTaskScoreOfLint("E:\\log\\5120152518.log", map);
	        });
		});
	}
	
	
	@Test
	@DisplayName("testGetTaskScore => 解析没有错误的日志-cpplint")
	void testGetTaskScoreOfLint2() {
		assertAll(()->{
			Map<String, Float> map = new HashMap<String, Float>();
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 55.0f);
			float score = RunTask.getTaskScoreOfLint("E:\\log\\5120151234.log", map);
			assertEquals(55.0f,score);
			
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
			//得分不为0,但error、style类错误个数大于5该类得分置为0
			Map<String, Float> map = new HashMap<String, Float>();
			map.put("error", 15.0f);
			map.put("style", 10.0f);
			map.put("warning", 3.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\someError.log", map);
			assertEquals(1.0f,score);

			//得分为0
			map.put("error", 4.0f);
			map.put("style", 3.0f);
			map.put("warning", 1.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\someError.log", map);
			assertEquals(0.0f,score);
			
			//设置了检查项但没有错误，成绩累加
			map.put("error", 15.0f);
			map.put("style", 10.0f);
			map.put("warning", 3.0f);
			score = RunTask.getTaskScoreOfCheck("E:\\cppcheckLog\\noErrorTest.log", map);
			assertEquals(28.0f,score);
			
			BufferedReader read = new BufferedReader(new FileReader("E:\\cppcheckLog\\noErrorTest.log"));
			assertEquals("Great! Check it out!",read.readLine());
			BufferedWriter write = new BufferedWriter(new FileWriter("E:\\cppcheckLog\\noErrorTest.log"));
			write.write("");
			read.close();
			write.close();
		});
	}
}
