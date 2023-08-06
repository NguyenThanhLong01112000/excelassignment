package excelassignment;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Employee {
	private String code;
	private String name;

	private TreeMap<Date, WorkDay> workingInfo;
	private HashMap<String, Double> payInfo;

	public Employee() {
		this.workingInfo = new TreeMap<>();
		this.payInfo = new HashMap<>();
	}

	public void printWorkingInfo() {
		System.out.println("Ten NV = " + name);
		System.out.println("Ma NV = " + code);
		Set<Entry<Date, WorkDay>> workingInfoSet = workingInfo.entrySet();
		Iterator<Entry<Date, WorkDay>> it = workingInfoSet.iterator();
		while (it.hasNext()) {
			Entry<Date, WorkDay> workDayEntry = it.next();

			WorkDay workDay = workDayEntry.getValue();
			Date date = workDayEntry.getKey();

			System.out.println(DateHelper.convertDate(date));

			HashMap<String, WorkShift> wordShiftMap = workDay.getWordShiftMap();

			Set<Entry<String, WorkShift>> wordShiftSet = wordShiftMap.entrySet();
			Iterator<Entry<String, WorkShift>> itShift = wordShiftSet.iterator();

			while (itShift.hasNext()) {
				Entry<String, WorkShift> shiftEntry = itShift.next();

				WorkShift workShift = shiftEntry.getValue();
				System.out.println(workShift.getWorkShiftType());

				HashMap<String, Work> workMap = workShift.getWorkMap();

				Set<Entry<String, Work>> workSet = workMap.entrySet();
				Iterator<Entry<String, Work>> itWork = workSet.iterator();

				while (itWork.hasNext()) {
					Entry<String, Work> workEntry = itWork.next();

					Work work = workEntry.getValue();
					System.out.print(workEntry.getKey() + ": ");
					System.out.print(work.getWorkHour());
					System.out.println();

				}
			}
		}
		System.out.println();
	}

	public void printTotalMoneyOfDay() {
		System.out.println("Ten NV = " + name);
		System.out.println("Ma NV = " + code);
		Set<Entry<Date, WorkDay>> workingInfoSet = workingInfo.entrySet();
		Iterator<Entry<Date, WorkDay>> it = workingInfoSet.iterator();

		while (it.hasNext()) {
			double totalMoney = 0.0;
			double totalWorkHour = 0.0;

			Entry<Date, WorkDay> workDayEntry = it.next();

			WorkDay workDay = workDayEntry.getValue();
			Date date = workDayEntry.getKey();

			System.out.println(DateHelper.convertDate(date));

			HashMap<String, WorkShift> wordShiftMap = workDay.getWordShiftMap();

			Set<Entry<String, WorkShift>> wordShiftSet = wordShiftMap.entrySet();
			Iterator<Entry<String, WorkShift>> itShift = wordShiftSet.iterator();

			while (itShift.hasNext()) {
				Entry<String, WorkShift> shiftEntry = itShift.next();

				WorkShift workShift = shiftEntry.getValue();

				HashMap<String, Work> workMap = workShift.getWorkMap();

				Set<Entry<String, Work>> workSet = workMap.entrySet();
				Iterator<Entry<String, Work>> itWork = workSet.iterator();

				while (itWork.hasNext()) {
					Entry<String, Work> workEntry = itWork.next();

					Work work = workEntry.getValue();
					String workType = work.getWorkType();
					double payPerHour = payInfo.get(workType);
					double workHour = work.getWorkHour();
					totalWorkHour += workHour;
					totalMoney += workHour * payPerHour;

				}

			}
			System.out.println("Thoi gian lam viec: " + totalWorkHour + " gio");
			System.out.println("Tien luong nhan duoc: " + totalMoney);
		}
	}

	public void printTotalMoney() {
		System.out.println("Ten NV = " + name);
		System.out.println("Ma NV = " + code);
		Set<Entry<Date, WorkDay>> workingInfoSet = workingInfo.entrySet();
		Iterator<Entry<Date, WorkDay>> it = workingInfoSet.iterator();
		double totalMoney = 0.0;

		while (it.hasNext()) {

			Entry<Date, WorkDay> workDayEntry = it.next();

			WorkDay workDay = workDayEntry.getValue();
			Date date = workDayEntry.getKey();

			HashMap<String, WorkShift> wordShiftMap = workDay.getWordShiftMap();

			Set<Entry<String, WorkShift>> wordShiftSet = wordShiftMap.entrySet();
			Iterator<Entry<String, WorkShift>> itShift = wordShiftSet.iterator();

			while (itShift.hasNext()) {
				Entry<String, WorkShift> shiftEntry = itShift.next();

				WorkShift workShift = shiftEntry.getValue();

				HashMap<String, Work> workMap = workShift.getWorkMap();

				Set<Entry<String, Work>> workSet = workMap.entrySet();
				Iterator<Entry<String, Work>> itWork = workSet.iterator();

				while (itWork.hasNext()) {
					Entry<String, Work> workEntry = itWork.next();

					Work work = workEntry.getValue();
					String workType = work.getWorkType();
					double payPerHour = payInfo.get(workType);
					double workHour = work.getWorkHour();
					totalMoney += workHour * payPerHour;

				}
			}
		}
		System.out.println("Tien luong nhan duoc: " + Math.round(totalMoney));
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeMap<Date, WorkDay> getWorkingInfo() {
		return workingInfo;
	}

	public HashMap<String, Double> getPayInfo() {
		return payInfo;
	}

}
