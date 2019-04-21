package swust.yang.entity;

public class CpplintConfigInfo {
	
	/**
	 * 该项检查总分
	 */
    private Float totalScore;
	
	/**
	 * 检查函数注释
	 */
	private String checkFuncAnnotation;
	
	/**
	 * 函数注释检查项设置的分数
	 */
	private Float scoreOfFuncAnnotation;
	
	/**
	 * 检查函数命名
	 */
	private String checkFuncName;
	
	/**
	 * 函数命名检查项设置的分数
	 */
	private Float scoreOfFuncName;
	
	/**
	 * 检查函数参数个数
	 */
	private String checkFuncParamtersNum;
	
	/**
	 * 函数参数个数检查项设置的分数
	 */
	private Float scoreOfFuncParamtersNum;
	
	/**
	 * 检查函数内部语句行数
	 */
	private String checkFuncStatLinesNum;
	
	/**
	 *函数内部语句行数检查项设置的分数
	 */
	private Float scoreOfFuncStatLinesNum;
	
	/**
	 * 检查变量命名
	 */
	private String checkVariableName;
	
	/**
	 * 变量命名检查项设置的分数
	 */
	private Float scoreOfVariableName;
	
	/**
	 * 检查宏常量命名
	 */
	private String checkMacroName;
	
	/**
	 * 宏常量命名检查项设置的分数
	 */
	private Float scoreOfMacroName;
	
	/**
	 * 检查嵌套次数
	 */
	private String checkNestedNum;
	
	/**
	 * 嵌套次数检查项设置的分数
	 */
	private Float scoreOfNestedNum;
	
	/**
	 * 检查是否使用goto语句
	 */
	private String checkUseGoto;
    
	/**
	 * 是否使用goto语句检查项设置的分数
	 */
	private Float scoreOfUseGoto;
	
	/**
	 * 检查每行代码的长度
	 */
	private String checkLineLength;
	
	/**
	 * 每行代码的长度检查项设置的分数
	 */
	private Float scoreOfLineLength;
	
	/**
	 * 检查代码缩进格式
	 */
	private String checkIdentationStyle;
	
	/**
	 *代码缩进格式检查项设置的分数
	 */
	private Float scoreOfIdentationStyle;
	
	/**
	 * 检查操作符周围是否使用空格
	 */
	private String checkOperationSpace;
	
	/**
	 *操作符周围是否使用空格检查项设置的分数
	 */
	private Float scoreOfOperationSpace;
	
	/**
	 * 检查关键词内部为一条语句时，是否使用大括号
	 */
	private String checkKeyWordsUseBraces;
	
	/**
	 *关键词内部为一条语句时，是否使用大括号检查项设置的分数
	 */
	private Float scoreOfKeyWordsUseBraces;
    
	/**
     * 扩展检查项的规则设置
     */
    private String checkExtendRules;
    
    /**
	 * 扩展检查项设置的分数
	 */
	private Float scoreOfExtendRules;

	public Float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Float totalScore) {
		this.totalScore = totalScore;
	}

	public String getCheckFuncAnnotation() {
		return checkFuncAnnotation;
	}

	public void setCheckFuncAnnotation(String checkFuncAnnotation) {
		this.checkFuncAnnotation = checkFuncAnnotation;
	}

	public Float getScoreOfFuncAnnotation() {
		return scoreOfFuncAnnotation;
	}

	public void setScoreOfFuncAnnotation(Float scoreOfFuncAnnotation) {
		this.scoreOfFuncAnnotation = scoreOfFuncAnnotation;
	}

	public String getCheckFuncName() {
		return checkFuncName;
	}

	public void setCheckFuncName(String checkFuncName) {
		this.checkFuncName = checkFuncName;
	}

	public Float getScoreOfFuncName() {
		return scoreOfFuncName;
	}

	public void setScoreOfFuncName(Float scoreOfFuncName) {
		this.scoreOfFuncName = scoreOfFuncName;
	}

	public String getCheckFuncParamtersNum() {
		return checkFuncParamtersNum;
	}

	public void setCheckFuncParamtersNum(String checkFuncParamtersNum) {
		this.checkFuncParamtersNum = checkFuncParamtersNum;
	}

	public Float getScoreOfFuncParamtersNum() {
		return scoreOfFuncParamtersNum;
	}

	public void setScoreOfFuncParamtersNum(Float scoreOfFuncParamtersNum) {
		this.scoreOfFuncParamtersNum = scoreOfFuncParamtersNum;
	}

	public String getCheckFuncStatLinesNum() {
		return checkFuncStatLinesNum;
	}

	public void setCheckFuncStatLinesNum(String checkFuncStatLinesNum) {
		this.checkFuncStatLinesNum = checkFuncStatLinesNum;
	}

	public Float getScoreOfFuncStatLinesNum() {
		return scoreOfFuncStatLinesNum;
	}

	public void setScoreOfFuncStatLinesNum(Float scoreOfFuncStatLinesNum) {
		this.scoreOfFuncStatLinesNum = scoreOfFuncStatLinesNum;
	}

	public String getCheckVariableName() {
		return checkVariableName;
	}

	public void setCheckVariableName(String checkVariableName) {
		this.checkVariableName = checkVariableName;
	}

	public Float getScoreOfVariableName() {
		return scoreOfVariableName;
	}

	public void setScoreOfVariableName(Float scoreOfVariableName) {
		this.scoreOfVariableName = scoreOfVariableName;
	}

	public String getCheckMacroName() {
		return checkMacroName;
	}

	public void setCheckMacroName(String checkMacroName) {
		this.checkMacroName = checkMacroName;
	}

	public Float getScoreOfMacroName() {
		return scoreOfMacroName;
	}

	public void setScoreOfMacroName(Float scoreOfMacroName) {
		this.scoreOfMacroName = scoreOfMacroName;
	}

	public String getCheckNestedNum() {
		return checkNestedNum;
	}

	public void setCheckNestedNum(String checkNestedNum) {
		this.checkNestedNum = checkNestedNum;
	}

	public Float getScoreOfNestedNum() {
		return scoreOfNestedNum;
	}

	public void setScoreOfNestedNum(Float scoreOfNestedNum) {
		this.scoreOfNestedNum = scoreOfNestedNum;
	}

	public String getCheckUseGoto() {
		return checkUseGoto;
	}

	public void setCheckUseGoto(String checkUseGoto) {
		this.checkUseGoto = checkUseGoto;
	}

	public Float getScoreOfUseGoto() {
		return scoreOfUseGoto;
	}

	public void setScoreOfUseGoto(Float scoreOfUseGoto) {
		this.scoreOfUseGoto = scoreOfUseGoto;
	}

	public String getCheckLineLength() {
		return checkLineLength;
	}

	public void setCheckLineLength(String checkLineLength) {
		this.checkLineLength = checkLineLength;
	}

	public Float getScoreOfLineLength() {
		return scoreOfLineLength;
	}

	public void setScoreOfLineLength(Float scoreOfLineLength) {
		this.scoreOfLineLength = scoreOfLineLength;
	}

	public String getCheckIdentationStyle() {
		return checkIdentationStyle;
	}

	public void setCheckIdentationStyle(String checkIdentationStyle) {
		this.checkIdentationStyle = checkIdentationStyle;
	}

	public Float getScoreOfIdentationStyle() {
		return scoreOfIdentationStyle;
	}

	public void setScoreOfIdentationStyle(Float scoreOfIdentationStyle) {
		this.scoreOfIdentationStyle = scoreOfIdentationStyle;
	}

	public String getCheckOperationSpace() {
		return checkOperationSpace;
	}

	public void setCheckOperationSpace(String checkOperationSpace) {
		this.checkOperationSpace = checkOperationSpace;
	}

	public Float getScoreOfOperationSpace() {
		return scoreOfOperationSpace;
	}

	public void setScoreOfOperationSpace(Float scoreOfOperationSpace) {
		this.scoreOfOperationSpace = scoreOfOperationSpace;
	}

	public String getCheckKeyWordsUseBraces() {
		return checkKeyWordsUseBraces;
	}

	public void setCheckKeyWordsUseBraces(String checkKeyWordsUseBraces) {
		this.checkKeyWordsUseBraces = checkKeyWordsUseBraces;
	}

	public Float getScoreOfKeyWordsUseBraces() {
		return scoreOfKeyWordsUseBraces;
	}

	public void setScoreOfKeyWordsUseBraces(Float scoreOfKeyWordsUseBraces) {
		this.scoreOfKeyWordsUseBraces = scoreOfKeyWordsUseBraces;
	}

	public String getCheckExtendRules() {
		return checkExtendRules;
	}

	public void setCheckExtendRules(String checkExtendRules) {
		this.checkExtendRules = checkExtendRules;
	}

	public Float getScoreOfExtendRules() {
		return scoreOfExtendRules;
	}

	public void setScoreOfExtendRules(Float scoreOfExtendRules) {
		this.scoreOfExtendRules = scoreOfExtendRules;
	}
	
}
