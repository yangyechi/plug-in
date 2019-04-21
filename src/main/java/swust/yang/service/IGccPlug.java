package swust.yang.service;

import java.util.List;

import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsgOfGcc;

public interface IGccPlug {

	/**
	 * 
	 * @description 用于编译、运行单个作业
	 * @param testCase  测试用例
	 * @param timeout   超时时间
	 * @param file_path 待运行的作业所在路径(绝对路径：包含文件名及文件后缀名)
	 * @param log_dir   日志存放目录(绝对路径)
	 * @return 若执行成功,则返回执行结果(包括学号和执行结果信息),反之返回null. 执行结果信息分七类:Accepted(通过)、Wrong
	 *         Answer(不通过)、Compile Error(编译错误)、 Time Limit Exceeded(超时)、Memory Limit
	 *         Exceeded(超出限定内存)、 Presentation Error(输出格式错误)、Runtime Error(运行错误)
	 */
	ResultMsgOfGcc singleExecute(List<String> testCase, long timeout, String file_path, String log_dir);

	/**
	 * @description 批量执行一个目录下的所有作业
	 * @param testCase 测试用例
	 * @param timeout  超时时间
	 * @param src_dir  待检查的作业目录(绝对路径)
	 * @param log_dir  日志存放目录(绝对路径)
	 * @return 返回批量执行结果(包括学号和执行结果信息) 执行结果信息分七类:Accept(通过)、Wrong Answer(不通过)、Compile
	 *         Error(编译错误)、 Time Limit Exceeded(超时)、Memory Limit Exceeded(超出限定内存)、
	 *         Presentation Error(输出格式错误)、Runtime Error(运行错误)
	 */
	List<ResultMsgOfGcc> batchExecute(List<String> testCase, long timeout, String src_dir, String log_dir);

	/**
	 * 
	 * @return 返回对插件信息的描述(返回值为一个实体类对象,其中保存了插件信息)
	 */
	PluginInfo getPluginInfo();
}
