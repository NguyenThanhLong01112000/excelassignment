package excelassignment;

import java.util.Date;
import java.util.HashMap;

public class WorkDay {
	private HashMap<String, WorkShift> wordShiftMap;
	private Date date;

	public WorkDay(Date date) {
		this.wordShiftMap = new HashMap<>();
		this.date = date;
	}

	public HashMap<String, WorkShift> getWordShiftMap() {
		return wordShiftMap;
	}
}
