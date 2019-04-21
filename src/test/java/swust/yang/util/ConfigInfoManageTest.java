package swust.yang.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import swust.yang.entity.CpplintConfigInfo;

@DisplayName("插件配置信息管理——代码规范性检查")
class ConfigInfoManageTest {

	private CpplintConfigInfo config;
	
	@BeforeEach
	void init() throws Exception {
		config = new CpplintConfigInfo();
	}

	@AfterEach
	void tearDown() throws Exception {
		config = null;
	}

	@Test
	@DisplayName("RuleConfig => 基础规则写入文本文件")
	void testRuleConfig() {
		String rule1 = "~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl";
		String rule2 = "~ RULE_3_3_A_start_function_name_with_lowercase_unix";
		config.setCheckFuncAnnotation(rule1);
		config.setCheckFuncName(rule2);
		String filePath = "E:\\test\\filefilter.txt";
		ConfigInfoManage.ruleConfig(config, filePath);
		assertAll("basedRule",()->{
			File file = new File(filePath);
			assertTrue(file.exists(),"file not exists");
			BufferedReader reader = new BufferedReader(new FileReader(file));	
			String readLine = null;
			while((readLine = reader.readLine()) != null) {
				if(!readLine.equals(rule1)) {
					assertEquals(rule2,readLine);
				}
				else if(!readLine.equals(rule2)) {
					assertEquals(rule1,readLine);
				}
			}
			reader.close();
		});
	}

	@Test
	@DisplayName("RuleConfig => 扩展规则写入文本文件")
	void testRuleConfig2() {
		String rule = "~ RULE_3_1_A_do_not_start_filename_with_underbar;"
				+ "~ RULE_3_2_B_do_not_use_same_filename_more_than_once;";
		config.setCheckExtendRules(rule);
		String filePath = "E:\\test\\filefilter1.txt";
		ConfigInfoManage.ruleConfig(config, filePath);
		assertAll("extendRule",()->{
			File file = new File(filePath);
			assertTrue(file.exists(),"file not exists");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			assertEquals("~ RULE_3_1_A_do_not_start_filename_with_underbar",reader.readLine());
			assertEquals("~ RULE_3_2_B_do_not_use_same_filename_more_than_once",reader.readLine());
			reader.close();
		});
	}
	
	
	@Test
	@DisplayName("GetRulesAndScores => 获取基础规则及对应分数")
	void testGetRulesAndScores() {
		String rule1 = "~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl";
		String rule2 = "~ RULE_3_3_A_start_function_name_with_lowercase_unix";
		config.setCheckFuncAnnotation(rule1);
		config.setCheckFuncName(rule2);
		config.setScoreOfFuncAnnotation(19.99f);
		config.setScoreOfFuncName(29.99f);
		assertAll(()->{
			Map<String,Float> map = ConfigInfoManage.getRulesAndScores(config);
			for(String item : map.keySet()) {
				if(item.equals(rule1)) {
					assertEquals(19.99f,map.get(item).floatValue());
				}
				else {
					assertEquals(29.99f,map.get(item).floatValue());
				}
			}
		});
	}
	
	@Test
	@DisplayName("GetRulesAndScores => 获取扩展规则及对应分数")
	void testGetRulesAndScores2() {
		String rule = "~ RULE_3_1_A_do_not_start_filename_with_underbar;"
				+ "~ RULE_3_2_B_do_not_use_same_filename_more_than_once;";
		config.setCheckExtendRules(rule);
		config.setScoreOfExtendRules(55.0f);
		assertAll(()->{
			Map<String,Float> map = ConfigInfoManage.getRulesAndScores(config);
			for(String item : map.keySet()) {
				assertEquals(55.0f/map.size(),map.get(item).floatValue());
				System.out.println(item);
			}
		});
	}

	@Test
	@DisplayName("analysisConfigInfo => 基础规则解析(除缩进格式检查)")
	void testAnalysisConfigInfo() {
		config.setScoreOfFuncAnnotation(19.99f);
		assertAll(()->{
			assertEquals("19.99",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfFuncAnnotation"));
			assertEquals("null",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfOperationSpace"));
		});
		
	}
	
	@Test
	@DisplayName("analysisConfigInfo => 扩展规则解析")
	void testAnalysisConfigInfo2() {
		String rule = "~ RULE_3_1_A_do_not_start_filename_with_underbar;"
				+ "~ RULE_3_2_B_do_not_use_same_filename_more_than_once;";
		config.setCheckExtendRules(rule);
		config.setScoreOfExtendRules(55.0f);
		assertAll(()->{
			assertEquals("55.0+" + rule,
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfExtendRules"));
			config.setScoreOfExtendRules(null);
			assertEquals("null",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfExtendRules"));
		});
		
	}

	@Test
	@DisplayName("analysisConfigInfo => 缩进规则解析")
	void testAnalysisConfigInfo3() {
		config.setCheckIdentationStyle("~ RULE_4_1_A_A_use_tab_for_indentation");
		config.setScoreOfIdentationStyle(18.8888f);
		assertAll(()->{
			assertEquals("18.8888+tab",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfIdentationStyle"));
			config.setCheckIdentationStyle("~ RULE_4_1_A_B_use_space_for_indentation");
			assertEquals("18.8888+space",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfIdentationStyle"));
			config.setScoreOfIdentationStyle(null);
			assertEquals("null",
					ConfigInfoManage.analysisConfigInfo(config, "getScoreOfIdentationStyle"));
		});
		
	}
	
	@Test
	@DisplayName("isRulesExist => 规则存在")
	void testIsRulesExist() {
		String[] array = {
				"~ RULE_4_1_B_indent_each_enum_item_in_enum_block",
				"~ RULE_4_1_B_locate_each_enum_item_in_seperate_line",
				"~ RULE_4_1_C_align_long_function_parameter_list",
				"~ RULE_4_1_E_align_conditions",
				"~ RULE_4_2_A_B_space_around_word"
		};
		assertEquals("OK",ConfigInfoManage.isRulesExist(array));
	}
	
	@Test
	@DisplayName("isRulesExist => 规则不存在或不符合规范")
	void testIsRulesExist2() {
		String[] array = {
				"123456789"
		};
		assertAll(()->{
			assertEquals("输入的规则(" + array[0] + ")不存在或者输入的规则不符合规范！请检查！",
					ConfigInfoManage.isRulesExist(array));
			array[0] = "~ RULE_4_2_A_B_space_around_word；";
			assertEquals("输入的规则(" + array[0] + ")不存在或者输入的规则不符合规范！请检查！",
					ConfigInfoManage.isRulesExist(array));
		});
		
	}
	
	@Test
	@DisplayName("isRulesExist => 规则重复")
	void testIsRulesExist3() {
		String[] array = {
				"~ RULE_4_1_B_indent_each_enum_item_in_enum_block",
				"~ RULE_4_1_B_locate_each_enum_item_in_seperate_line",
				"~ RULE_4_1_C_align_long_function_parameter_list",
				"~ RULE_4_1_E_align_conditions",
				"~ RULE_4_2_A_B_space_around_word",
				"~ RULE_4_1_B_indent_each_enum_item_in_enum_block",
		};
		assertEquals("输入的规则(" + array[0] + ")重复！请检查！",
				ConfigInfoManage.isRulesExist(array));
	}

}
