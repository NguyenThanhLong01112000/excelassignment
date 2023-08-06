package excelassignment;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		String fileExcel = "src/main/resources/BangCong.xlsx";
		readExcelFile(fileExcel);
		printSalaryInfo();
	}

	private static void readExcelFile(String path) {
		try {
			SalaryManager.getInstance().readExcelFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printSalaryInfo() {
		SalaryManager.getInstance().printSalaryInfo();
	}
}
