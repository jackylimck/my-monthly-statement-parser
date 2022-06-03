package MonthlyStatementParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class App1 {

	public static void main(String[] args) {

		Properties prop = loadConfigFile();

		if (prop.size() > 0) {

			// Process Simply Go Statement
			String simplygoDir = prop.getProperty("simplygo.dir");
			List<String> simplygoStmts = Arrays.asList(prop.getProperty("simplygo.stmt.list").split(","));

			Double simplyGo = processSimplyGoStatements(simplygoDir, simplygoStmts);

			// Process DBS Statement
			String dbsDir = prop.getProperty("dbs.dir");
			List<String> dbsStmtList = Arrays.asList(prop.getProperty("dbs.stmt.list").split(","));
			String dbsKeyword = prop.getProperty("dbs.keyword");

			Double dbsAltitude = processDBSAltitudeStatements(dbsDir, dbsStmtList, dbsKeyword);

			// Comparison
			 System.out.println(Double.compare(simplyGo, dbsAltitude) == 0);

		} else {

			System.out.println("Try again");
		}

	}

	private static Double processSimplyGoStatements(String directory, List<String> inputFileNames) {

		Double total = 0.0;

		String keyword = "POSTED";

		for (String input : inputFileNames) {
			File file = new File(directory + input);
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
		}

		System.out.println(String.format("SIMPLY GO TOTAL: $%.2f", total));

		return total;
	}

	private static Double processDBSAltitudeStatements(String directory, List<String> inputFileNames, String keyword) {

		Double total = 0.0;

		Double totalGrab = 0.0;

		// String outputFile = "C:\\Users\\Jacky\\Desktop\\output.csv";

		// BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		for (String input : inputFileNames) {
			File file = new File(directory + input);
			PDDocument doc = null;
			
			try {
				
				doc = PDDocument.load(file);				
				PDFTextStripper pdfStripper = new PDFTextStripper();

				String extractedText = pdfStripper.getText(doc);

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
		}
//		writer.flush();
//		writer.close();

		// System.out.println(String.format("GRAB TOTAL: $%.2f", totalGrab));
		System.out.println(String.format("DBS TOTAL: $%.2f", total));

		return total;
	}
	
	private static Properties loadConfigFile() {

		String configFileName = "config.properties";

		Properties prop = new Properties();

		File configFile = new File(configFileName);

		FileReader reader;
		try {
			reader = new FileReader(configFile);
			prop.load(reader);

		} catch (FileNotFoundException e) {

			System.out.println("Unable to load config file: " + configFileName);
		} catch (IOException e) {

			e.printStackTrace();
		}

		return prop;
	}

}
