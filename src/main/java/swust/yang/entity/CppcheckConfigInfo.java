package swust.yang.entity;

public class CppcheckConfigInfo {
	
	/**
	 * 为了避免产生bug而提供的编程改进意见
	 */
	private String checkWarning;
	
	/**
	 * 检查代码简洁性（变量未使用、冗余代码等）。
	 */
	private String checkStyle;
	
	/**
	 * 检查代码的可移植性(64/32位可移植性、编译器通用性等)
	 */
	private String checkPortability;
	
	/**
	 * 检查代码的性能(使代码更高效的建议，但不保证一定有明显效果)
	 */
	private String checkPerformance;
	
	private Float totalScore;
	
	private Float scoreOfError;
	
	private Float scoreOfWarning;
	
	private Float scoreOfStyle;
	
	private Float scoreOfPortability;
	
	private Float scoreOfPerformance;

	public String getCheckWarning() {
		return checkWarning;
	}

	public void setCheckWarning(String checkWarning) {
		this.checkWarning = checkWarning;
	}

	public String getCheckStyle() {
		return checkStyle;
	}

	public void setCheckStyle(String checkStyle) {
		this.checkStyle = checkStyle;
	}

	public String getCheckPortability() {
		return checkPortability;
	}

	public void setCheckPortability(String checkPortability) {
		this.checkPortability = checkPortability;
	}

	public String getCheckPerformance() {
		return checkPerformance;
	}

	public void setCheckPerformance(String checkPerformance) {
		this.checkPerformance = checkPerformance;
	}
	
	public Float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Float totalScore) {
		this.totalScore = totalScore;
	}

	public Float getScoreOfError() {
		return scoreOfError;
	}

	public void setScoreOfError(Float scoreOfError) {
		this.scoreOfError = scoreOfError;
	}

	public Float getScoreOfWarning() {
		return scoreOfWarning;
	}

	public void setScoreOfWarning(Float scoreOfWarning) {
		this.scoreOfWarning = scoreOfWarning;
	}

	public Float getScoreOfStyle() {
		return scoreOfStyle;
	}

	public void setScoreOfStyle(Float scoreOfStyle) {
		this.scoreOfStyle = scoreOfStyle;
	}

	public Float getScoreOfPortability() {
		return scoreOfPortability;
	}

	public void setScoreOfPortability(Float scoreOfPortability) {
		this.scoreOfPortability = scoreOfPortability;
	}

	public Float getScoreOfPerformance() {
		return scoreOfPerformance;
	}

	public void setScoreOfPerformance(Float scoreOfPerformance) {
		this.scoreOfPerformance = scoreOfPerformance;
	}
	
}
