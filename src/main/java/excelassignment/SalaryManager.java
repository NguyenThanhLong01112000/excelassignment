package excelassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SalaryManager {
	private static SalaryManager instance;
	public static final int CODE_COLUMN = 1;
	public static final int NAME_COLUMN = 2;

	private TreeMap<String, Employee> employeeInfo;

	public SalaryManager() {
		employeeInfo = new TreeMap<>();
	}

	public static SalaryManager getInstance() {
		if (instance == null) {
			instance = new SalaryManager();
		}
		return instance;
	}

	public void readExcelFile(String path) throws IOException {
		Workbook workbook = new XSSFWorkbook(path);
		Sheet sheet = workbook.getSheetAt(0);
		workbook.close();

		readMonthYear(sheet);
		readDayList(sheet);
		readWorkTypeList(sheet);
		readWorkingInfo(sheet);
	}

	public void printSalaryInfo() {
		Set<Entry<String, Employee>> employeeSet = employeeInfo.entrySet();
		Iterator<Entry<String, Employee>> it = employeeSet.iterator();
		while (it.hasNext()) {
			Entry<String, Employee> entry = it.next();
			Employee e = entry.getValue();
			// e.printWorkingInfo();
			// e.printTotalMoneyOfDay();
			e.printTotalMoney();
		}
	}

	private void readMonthYear(Sheet sheet) {
		Row rowMonth = sheet.getRow(0);
		Cell cellMonth = rowMonth.getCell(1);
		DateHelper.staticMonth = (int) cellMonth.getNumericCellValue();

		Row rowYear = sheet.getRow(1);
		Cell cellYear = rowYear.getCell(1);
		DateHelper.staticYear = (int) cellYear.getNumericCellValue();
	}

	private void readDayList(Sheet sheet) {
		Row rowDayList = sheet.getRow(3);
		Iterator<Cell> cellIterator = rowDayList.cellIterator();
		int daySize = 0;
		boolean reachedFirstDay = false;
		int lastDay = 0;
		int currentMonth = DateHelper.staticMonth;
		int currentYear = DateHelper.staticYear;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			CellType cellType = cell.getCellType();

			if (cellType == CellType.BLANK && reachedFirstDay) {
				daySize++;
				continue;
			} else if (cellType != CellType.NUMERIC) {
				continue;
			}

			if (!reachedFirstDay) {
				reachedFirstDay = true;
				DateHelper.columnOfFirstDay = cell.getColumnIndex();
				daySize = 1;
				lastDay = (int) cell.getNumericCellValue();
				continue;
			} else {
				Date date = new Date(currentYear, currentMonth - 1, lastDay);
				DateHelper.staticDayMap.put(date, daySize);
			}

			daySize = 1;
			lastDay = (int) cell.getNumericCellValue();

			if (lastDay != 1) {

			} else if (currentMonth != 12) {
				currentMonth++;
			} else {
				currentMonth = 1;
				currentYear++;
			}

			if (lastDay == -1)
				break;
		}
	}

	private void readWorkTypeList(Sheet sheet) {
		Row rowWorkList = sheet.getRow(5);
		ArrayList<String> workType = new ArrayList<>();
		for (int i = 3;; i++) {
			Cell cell = rowWorkList.getCell(i);
			String cellValue = cell.getStringCellValue();

			// khi đọc đến ô gộp "Tổng lương" thì dừng lại
			if (cellValue.isBlank()) {
				break;
			}

			if (cellValue.equals("$")) {
				for (int j = 0; j < workType.size(); j++) {
					Work.workTypeAndPayColumn.put(workType.get(j), i);
				}
				workType.clear();
				continue;
			}

			workType.add(cellValue);
		}
	}

	private void readWorkingInfo(Sheet sheet) {
		// đọc từ hàng số 07 trở đi
		for (int i = 6;; i++) {
			Row row = sheet.getRow(i);
			if (isRowEmpty(row))
				break; // đã đọc hết các dòng

			Employee e = new Employee();

			if (!readEmployeeCode(sheet, i, e))
				continue;

			if (!readEmployeeName(sheet, i, e))
				continue;

			readEmployeePayInfo(sheet, i, e);

			readEmployeeWorkingInfo(sheet, i, e);

			employeeInfo.put(e.getCode(), e);
		}
	}

	private static void readEmployeeWorkingInfo(Sheet sheet, int employeeRowNumber, Employee e) {
		Row rowDayList = sheet.getRow(3);

		TreeMap<Date, WorkDay> workingInfo = e.getWorkingInfo();
		int firstDayColumn = DateHelper.columnOfFirstDay;
		int currentMonth = DateHelper.staticMonth;
		int currentYear = DateHelper.staticYear;

		boolean reachedFirstDay = false;

		for (int firstColumnOfDay = firstDayColumn;;) {
			Cell dayCell = rowDayList.getCell(firstColumnOfDay);
			CellType dayCellType = dayCell.getCellType();
			if (dayCellType != CellType.NUMERIC) {
				break;
			}
			int day = (int) dayCell.getNumericCellValue();

			if (!reachedFirstDay) {
				reachedFirstDay = true;
			} else if (day != 1) {

			} else if (currentMonth != 12) {
				currentMonth++;
			} else {
				currentMonth = 1;
				currentYear++;
			}

			if (day == -1)
				break;
			Date date = new Date(currentYear, currentMonth - 1, day);
			int daySize = DateHelper.staticDayMap.get(date);
			WorkDay workDay = new WorkDay(date);

			readWorkShiftOfDay(sheet, workDay, firstColumnOfDay, daySize, employeeRowNumber);

			workingInfo.put(date, workDay);
			firstColumnOfDay += daySize;
		}

	}

	private static void readWorkShiftOfDay(Sheet sheet, WorkDay workDay, int firstColumnOfDay, int daySize,
			int employeeRowNumber) {
		Row shiftRow = sheet.getRow(4);
		Row workTypeRow = sheet.getRow(5);
		Row workRow = sheet.getRow(employeeRowNumber);
		HashMap<String, WorkShift> wordShiftMap = workDay.getWordShiftMap();

		HashMap<String, Integer> localShiftSizeMap = new HashMap<>();
		readShiftSize(shiftRow, firstColumnOfDay, daySize, localShiftSizeMap);

		for (int i = firstColumnOfDay; i < firstColumnOfDay + daySize; i++) {
			Cell shiftCell = shiftRow.getCell(i);
			CellType shiftCellType = shiftCell.getCellType();
			if (shiftCellType == CellType.BLANK) {
				continue;
			}

			String shiftName = shiftCell.getStringCellValue();
			int shiftSize = localShiftSizeMap.get(shiftName);
			int firstColumnOfShift = i;
			WorkShift workShift = new WorkShift(shiftName);

			readWorkOfShift(firstColumnOfShift, shiftSize, workTypeRow, workRow, workShift);
			wordShiftMap.put(shiftName, workShift);

		}

	}

	private static void readWorkOfShift(int firstColumnOfShift, int shiftSize, Row workTypeRow, Row workRow,
			WorkShift workShift) {
		HashMap<String, Work> workMap = workShift.getWorkMap();

		for (int i = firstColumnOfShift; i < firstColumnOfShift + shiftSize; i++) {
			Cell workTypeCell = workTypeRow.getCell(i);
			CellType workTypeCellType = workTypeCell.getCellType();

			if (workTypeCellType == CellType.BLANK) {
				System.out.printf("Thong tin ve loai cong lam viec khong hop le tai o %c%d\n", 'A' + i,
						workTypeRow.getRowNum() + 1);
				continue;
			}

			String workType = workTypeCell.getStringCellValue();
			if (!Work.workTypeAndPayColumn.containsKey(workType)) {
				System.out.printf(
						"Thong tin ve loai cong lam viec khong hop le tai o %c%d: loai cong lam viec khong duoc dinh nghia truoc\n",
						'A' + i, workTypeRow.getRowNum() + 1);
				continue;
			}

			Cell workHourCell = workRow.getCell(i);
			CellType workHourCellType = workHourCell.getCellType();
			double hour = 0.0;

			if (workHourCellType == CellType.NUMERIC) {
				hour = workHourCell.getNumericCellValue();
			} else if (workHourCellType == CellType.BLANK) {
				hour = 0.0;
			} else {
				System.out.printf("So gio cong khong hop le tai o  %c%d\n", 'A' + workRow.getRowNum(), i + 1);
				continue;
			}

			Work work = new Work(workType, hour);
			workMap.put(workType, work);
		}
	}

	private static void readShiftSize(Row shiftRow, int firstColumnOfDay, int daySize,
			HashMap<String, Integer> localShiftSizeMap) {
		int shiftSize = 0;
		String lastShiftName = "";
		boolean reachedFirstShift = false;

		for (int i = firstColumnOfDay; i <= firstColumnOfDay + daySize; i++) {
			if (i == firstColumnOfDay + daySize) {
				localShiftSizeMap.put(lastShiftName, shiftSize);
				continue;
			}

			Cell shiftCell = shiftRow.getCell(i);
			CellType shiftCellType = shiftCell.getCellType();
			if (shiftCellType == CellType.STRING && !reachedFirstShift) {
				lastShiftName = shiftCell.getStringCellValue();
				reachedFirstShift = true;
				shiftSize++;
			} else if (shiftCellType == CellType.STRING && reachedFirstShift) {
				localShiftSizeMap.put(lastShiftName, shiftSize);

				lastShiftName = shiftCell.getStringCellValue();
				shiftSize = 1;
			} else {
				shiftSize++;
			}
		}
	}

	private static boolean readEmployeeCode(Sheet sheet, int rowNumber, Employee e) {
		int codeColumnNumber = CODE_COLUMN; // đọc cột B
		Row row = sheet.getRow(rowNumber);
		Cell codeCell = row.getCell(codeColumnNumber);
		String code = codeCell.getStringCellValue();

		if (code.isBlank()) {
			System.out.printf("Ma nhan vien khong hop le tai o %c%d\n", 'A' + codeColumnNumber, rowNumber + 1);
			return false;
		}

		e.setCode(code);
		return true;
	}

	private static boolean readEmployeeName(Sheet sheet, int rowNumber, Employee e) {
		int nameColumnNumber = NAME_COLUMN; // đọc cột C
		Row row = sheet.getRow(rowNumber);
		Cell nameCell = row.getCell(nameColumnNumber);
		String name = nameCell.getStringCellValue();

		if (name.isBlank()) {
			System.out.printf("Ten nhan vien khong hop le tai o %c%d\n", 'A' + nameColumnNumber, rowNumber + 1);
			return false;
		}

		e.setName(name);
		return true;
	}

	private static void readEmployeePayInfo(Sheet sheet, int rowNumber, Employee e) {
		Row row = sheet.getRow(rowNumber);
		HashMap<String, Double> payInfo = e.getPayInfo();
		Set<String> workTypeSet = Work.workTypeAndPayColumn.keySet();
		Iterator<String> it = workTypeSet.iterator();
		while (it.hasNext()) {
			String workType = it.next();
			int column = Work.workTypeAndPayColumn.get(workType);
			Cell payInfoCell = row.getCell(column);
			CellType cellType = payInfoCell.getCellType();
			double cellValue = 0.0;

			if (cellType == CellType.NUMERIC) {
				cellValue = payInfoCell.getNumericCellValue();
			} else if (cellType == CellType.FORMULA) {
				Workbook workbook = payInfoCell.getSheet().getWorkbook();
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				cellValue = evaluator.evaluate(payInfoCell).getNumberValue();
			} else if (cellType == CellType.BLANK) {
				cellValue = 0.0;
			} else {
				System.out.printf("Tien cong khong hop le tai o  %c%d\n", 'A' + column, rowNumber + 1);
				continue;
			}
			payInfo.put(workType, cellValue);
		}
	}

	private static boolean isRowEmpty(Row row) {
		for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
			Cell cell = row.getCell(c);
			if (cell != null && cell.getCellType() != CellType.BLANK)
				return false;
		}
		return true;
	}
}
