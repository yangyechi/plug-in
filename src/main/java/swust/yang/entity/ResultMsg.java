package swust.yang.entity;

public class ResultMsg {
	
	/**
	 * 学生信息
	 */
	private String studentInfor;
	
	/**
	 * 作业得分
	 */
	private float score;

	public String getStudentInfor() {
		return studentInfor;
	}

	public void setStudentInfor(String studentInfor) {
		this.studentInfor = studentInfor;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
