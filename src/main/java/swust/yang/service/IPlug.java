package swust.yang.service;

import java.util.List;

import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsg;

public interface IPlug {
	/**
	 * 
	 * @description 用于执行单个作业
	 * @param toolPath   执行所需的外部工具的根目录(绝对路径)
	 * @param configInfo 前端返回的配置信息(JSON格式)
	 * @param filePath   待检查作业所在路径(绝对路径：包含文件名及文件后缀名)
	 * @param logDir     日志存放目录(绝对路径)
	 * @return 若执行成功，则返回执行结果(学生信息+得分(四舍五入取整))，反之返回null
	 */
	ResultMsg singleExecute(String configInfo, String toolPath, String filePath, String logDir);

	/**
	 * @description 批量执行一个目录下的所有作业
	 * @param toolPath   执行所需的外部工具的根目录(绝对路径)
	 * @param configInfo 前端返回的配置信息(JSON格式)
	 * @param srcDir     待检查的作业目录(绝对路径)
	 * @param logDir     日志存放目录(绝对路径)
	 * @return 批量执行结果(学生信息+得分(四舍五入取整))，若某学生执行过程出错,那么该学生得分为0
	 */
	List<ResultMsg> batchExecute(String configInfo, String toolPath, String srcDir, String logDir);

	/**
	 * @description 用于检查前端配置是否符合规范
	 * @param configInfo 前端返回的配置信息(JSON格式)
	 * @return 如果配置没有错误,则返回"OK",否则返回错误描述。
	 */
	String checkConfigInfo(String configInfo);

	/**
	 * 
	 * @return 返回对插件信息的描述(返回值为一个实体类对象,其中保存了插件信息)
	 */
	PluginInfo getPluginInfo();

	/***
	 * 
	 * @param preSetting 之前的配置信息(JSON格式),第一次配置时参数置为null
	 * @return 返回插件配置前端界面
	 */
	String getHtml(String preSetting);
}
