package excelassignment;

import java.util.Date;
import java.util.HashMap;

public class DateHelper {
	public static HashMap<Date, Integer> staticDayMap = new HashMap<>();
	public static int staticMonth;
	public static int staticYear;
	public static int columnOfFirstDay;

	public static String convertDate(Date date) {
		int year = date.getYear();
		int month = date.getMonth() + 1;
		int day = date.getDate();
		return "Ngay " + day + " thang " + month + " nam " + year;
	}
}
