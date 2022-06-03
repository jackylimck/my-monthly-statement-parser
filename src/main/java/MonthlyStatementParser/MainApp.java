package MonthlyStatementParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class MainApp {

	public static final String SIMPLY_GO_DIR = "\\\\192.168.1.123\\personal\\Transport\\";
//	public static final String SIMPLY_GO_DIR = "C:\\Users\\Turke\\Downloads\\";
	public static final List<String> SIMPLY_GO_STMT_LIST = Arrays.asList("2022-01_TL-SimplyGo-TransactionHistory.pdf",
			"2022-01_Revolut_TL-SimplyGo-TransactionHistory.pdf");

//	public static final String DBS_DIR = "\\\\192.168.1.123\\personal\\Bank Accounts & Credit Cards\\DBS\\DBS Altitude\\";
	public static final String DBS_DIR = "C:\\Users\\Turke\\Downloads\\";
	public static final List<String> DBS_STMT_LIST = Arrays.asList("2021-12_DBS_ALTITUDE.pdf",
			"2022-01_DBS_ALTITUDE.pdf");

	public static final String TRANSPORT_KEYWORD = "JAN BUS/MRT";

	public static void main(String[] args) {

		Double simplyGo = processSimplyGoStatements(SIMPLY_GO_DIR, SIMPLY_GO_STMT_LIST);

		Double dbsAltitude = processDBSAltitudeStatements(DBS_DIR, DBS_STMT_LIST, TRANSPORT_KEYWORD);

		System.out.println("---");
		System.out.println(simplyGo);
		System.out.println(dbsAltitude);
		System.out.println(Double.compare(simplyGo, dbsAltitude) == 0);
	}

	private static Double processSimplyGoStatements(String directory, List<String> inputFileNames) {

		Double total = 0.0;

		String keyword = "POSTED";

		for (String fileName : inputFileNames) {
			File file = new File(directory + fileName);

			PDDocument doc = null;
			try {
				doc = PDDocument.load(file);
				PDFTextStripper pdfStripper = new PDFTextStripper();

				String extractedText = pdfStripper.getText(doc);

				// split by detecting newline
				String[] lines = extractedText.split("\r\n|\r|\n");

				for (String line : lines) {

					if (line.contains(keyword)) {

						line = line.replaceAll(" ", "");

						Double cost = 0.0;

						try {

							cost = Double.parseDouble(line.substring(line.indexOf("$") + 1));
						} catch (NumberFormatException e) {

							System.out.println("Error: " + line);
						}

						total += cost;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}

		System.out.println(String.format("SIMPLY GO TOTAL: $%.2f", total));

		return total;

	}

	private static Double processDBSAltitudeStatements(String directory, List<String> inputFileNames, String keyword) {

		Double total = 0.0;

		Double totalGrab = 0.0;

		// String outputFile = "C:\\Users\\JackyLimCK\\Desktop\\output.csv";

		// BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		PDDocument doc = null;

		try {
			for (String fileName : inputFileNames) {

				File file = new File(directory + fileName);

				doc = PDDocument.load(file);
				PDFTextStripper pdfStripper = new PDFTextStripper();

				String extractedText = pdfStripper.getText(doc);
				System.out.println("************");
				System.out.println(extractedText);
				System.out.println("************");
				// split by detecting newline
				String[] lines = extractedText.split("\r\n|\r|\n");

				for (String line : lines) {

					if (line.contains("JUL GRAB")) {

						Double grabCost = 0.0;

						try {
							grabCost = Double.parseDouble(line.substring(line.length() - 5, line.length()));

						} catch (Exception e) {
							System.out.println(e);
						}

						totalGrab += grabCost;
						// System.out.println("totalGrab >> " + totalGrab);
					}

					if (line.contains(keyword)) {

						StringBuilder sb = new StringBuilder();

						line = line.replaceAll(" ", "");

						// System.out.println(temp.substring(19, temp.length()));
						Double cost = 0.0;

						try {

							int length = line.length();
							cost = Double.parseDouble(line.substring(length - 4, length));

							sb.append(cost);
							sb.append("\n");

							// System.out.println(cost);
						} catch (NumberFormatException e) {

							System.out.println("Line: " + line);
						}

						total += cost;

						// writer.append(sb.toString());
					}
				}

				doc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				doc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		writer.flush();
//		writer.close();

		// System.out.println(String.format("GRAB TOTAL: $%.2f", totalGrab));
		System.out.println(String.format("DBS TOTAL: $%.2f", total));

		return total;
	}

}
