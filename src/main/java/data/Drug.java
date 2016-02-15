package data;

public class Drug {
	
	//7;Drug Era - 30 day window;30;1549080;18
	
	private double drugExposureType;
	private String drugExposureDesc;
	private double persistenceWindow;
	private double drugId;
	private double daysAgo;
	
	public Drug (double drugExposureType, String drugExposureDesc, double persistenceWindow, double drugId, double daysAgo) {
		this.drugExposureType = drugExposureType;
		this.drugExposureDesc = drugExposureDesc;
		this.persistenceWindow = persistenceWindow;
		this.drugId = drugId;
		this.daysAgo = daysAgo;
	}

	public double getDrugExposureType() {
		return drugExposureType;
	}

	public String getDrugExposureDesc() {
		return drugExposureDesc;
	}

	public double getPersistenceWindow() {
		return persistenceWindow;
	}

	public double getDrugId() {
		return drugId;
	}

	public double getDaysAgo() {
		return daysAgo;
	}

}
