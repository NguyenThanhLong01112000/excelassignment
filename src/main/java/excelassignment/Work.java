package excelassignment;

import java.util.HashMap;

public class Work {
	public static HashMap<String, Integer> workTypeAndPayColumn = new HashMap<>();

	private String workType;
	private double workHour;

	public Work(String workType, double workHour) {
		this.workType = workType;
		this.workHour = workHour;
	}

	public String getWorkType() {
		return workType;
	}

	public double getWorkHour() {
		return workHour;
	}
}
