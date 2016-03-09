package tagcloud;

public class MatchedPair {
	
	private CodePair omopPair;
	private CodePair icdPair;
	
	public MatchedPair(CodePair omopPair, CodePair icdPair) {
		this.omopPair = omopPair;
		this.icdPair = icdPair;
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
	
	

}
