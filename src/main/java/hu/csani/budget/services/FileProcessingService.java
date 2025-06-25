package hu.csani.budget.services;

import com.github.pjfanning.xlsx.StreamingReader;
import hu.csani.budget.data.Budget;
import hu.csani.budget.data.UploadRule;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileProcessingService {


	public List<List<String>> readCSV(InputStream input) throws IOException {
		List<List<String>> allRows = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
			String line;
			while ((line = reader.readLine()) != null) {
				List<String> row = parseCSVLine(line);
				allRows.add(row);
			}
		}
		return allRows;
	}
	// Simple CSV parser (does not handle quoted fields with commas)
	private List<String> parseCSVLine(String line) {
		return Arrays.asList(line.split("\\s*,\\s*", -1));
	}
	
	
	
	
	
	public List<List<String>> readXLSX(InputStream tempFile, String sheetName)
			throws InvalidFormatException, IOException {

		List<List<String>> data = new ArrayList<>();

//		InputStream is = new FileInputStream(tempFile);
//		StreamingReader reader = StreamingReader.builder().rowCacheSize(100) // number																				 of																				 rows																				 to																				 keep																				 in																				 memory
////																				 (defaults
////																				 to
////																				 10)
//				.bufferSize(4096) // buffer size to use when reading InputStream
//				// to file (defaults to 1024)
//				.sheetIndex(0) // index of sheet to use (defaults to 0)
//				.read(is); // InputStream or File for XLSX file (required)

//		The StreamingWorkbook is an autocloseable resource, and it's important that you close it to free the filesystem resource it consumed. With Java 8, you can do this:
		try (Workbook open = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(tempFile);) {
			Sheet sheet = null;
			if (sheetName != null) {
				sheet = open.getSheet(sheetName);
			} else {
				sheet = open.getSheetAt(0);
			}
			// The first row defines how many columns we ready! To avoid any random trash in
			// the rows Wrapper needed because of lamda operator
			var wrapper = new Object() {
				int header = 0;
			};

			DecimalFormat df = new DecimalFormat("0.##########");
			// This force the decimal format to . intread of ,
			df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

			DataFormatter dataFormatter = new DataFormatter();
			dataFormatter.setUseCachedValuesForFormulaCells(true);
			dataFormatter.setUse4DigitYearsInAllDateFormats(true);

			sheet.rowIterator().forEachRemaining(e -> {

				List<String> stringArrayRow = new LinkedList<>();

				int physicalNumberOfCells = e.getPhysicalNumberOfCells();
				if (wrapper.header == 0)
					wrapper.header = physicalNumberOfCells;

				for (int i = 0; i < wrapper.header; i++) {
					Cell cell = e.getCell(i);
					String cellStringValue = null;
					if (cell != null) {
//						cellStringValue = cell.getStringCellValue();
//						System.out.println("Defaul: " + cellStringValue);
//						String value = dataFormatter.formatCellValue(cell); // from apache poi 5.2.0 on
//						System.out.println("Formatter: " + value);

						try {
							if (DateUtil.isCellDateFormatted(cell)) {
								String formatRawCellContents = dataFormatter
										.formatRawCellContents(cell.getNumericCellValue(), 59, "yyyy-mm-dd");
								cellStringValue = formatRawCellContents;
//								System.out.println(formatRawCellContents);
							}
							// Megpróbáljuk még számként beolvasni!
							if (cellStringValue == null && !cell.getStringCellValue().startsWith("0")) {
								cellStringValue = df.format(cell.getNumericCellValue());
							}
						} catch (NumberFormatException ne) {
							cellStringValue = cell.getStringCellValue();
						}
						// Ha csak egy sima 0-van a cellában, akkor is ez a megoldás.
						if (cellStringValue == null)
							cellStringValue = cell.getStringCellValue();
					}
					stringArrayRow.add(cellStringValue);
				}
				data.add(stringArrayRow);
			});
			return data;
		}
	}
}
