package swust.yang.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dos命令")
class RunTaskTest {

	@Test
	@DisplayName("runCommand => nsiqcppstyle命令")
	void testRunCommand() {
		String executeCommand = "python D:\\Eclipse(EE)4.8.0\\eclipse\\workspace\\plug_cpplint\\src\\main\\resources\\nsiqcppstyle\\nsiqcppstyle.py --output=csv -o E:\\log\\5120152516.log -f E:\\test\\filefilter.txt E:\\test\\5120152516.cpp";
		assertAll(()->{
			RunTask.runCommand(executeCommand);
			File file = new File("E:\\log\\5120152516.log");			
			assertTrue(file.exists());
		});	
	}

	@Test
	@DisplayName("testGetTaskScore => 解析日志得出分数")
	void testGetTaskScore() {
		Map<String, Float> map = new HashMap<String, Float>();
		map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 55.88f);
		assertAll(()->{
			float score = 0.0f;
			//得分不为0
			score = RunTask.getTaskScore("E:\\log\\5120152516.log", map);
			assertEquals(53.0f,score);
			
			//得分为0
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 1.0f);
			score = RunTask.getTaskScore("E:\\log\\5120152516.log", map);
			assertEquals(0.0f,score);
			
			//没有出错的规则，分数累加
			map.put("~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl", 4.5f);
			map.put("~ RULE_4_5_B_use_braces_even_for_one_statement", 8.5f);
			score = RunTask.getTaskScore("E:\\log\\5120152516.log", map);
			assertEquals(10.0f,score);
			
			assertThrows(FileNotFoundException.class, () -> {
				 RunTask.getTaskScore("E:\\log\\5120152518.log", map);
	        });
		});
	}

}
