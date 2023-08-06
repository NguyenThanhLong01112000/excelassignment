package excelassignment;

import java.util.HashMap;

public class WorkShift {
	private HashMap<String, Work> workMap;
	private String workShiftType;

	public WorkShift() {
		this.workMap = new HashMap<>();
	}

	public WorkShift(String workShiftName) {
		this.workMap = new HashMap<>();
		this.workShiftType = workShiftName;
	}

	public HashMap<String, Work> getWorkMap() {
		return workMap;
	}

	public String getWorkShiftType() {
		return workShiftType;
	}

	public void setWorkShiftType(String workShiftType) {
		this.workShiftType = workShiftType;
	}
}
