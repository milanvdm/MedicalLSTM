package tagcloud;

public class MatchedPair {
	
	private CodePair omopPair;
	private CodePair icdPair;
	private double maxScore;
	
	public MatchedPair(CodePair omopPair, CodePair icdPair, double maxScore) {
		this.omopPair = omopPair;
		this.icdPair = icdPair;
		this.maxScore = maxScore;
	}

	public CodePair getOmopPair() {
		return omopPair;
	}

	public void setOmopPair(CodePair omopPair) {
		this.omopPair = omopPair;
	}

	public CodePair getIcdPair() {
		return icdPair;
	}

	public void setIcdPair(CodePair icdPair) {
		this.icdPair = icdPair;
	}
	
	public double getScore() {
		return this.maxScore;
	}
	

}
