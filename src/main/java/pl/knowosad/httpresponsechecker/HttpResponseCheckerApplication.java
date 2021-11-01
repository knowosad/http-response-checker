package pl.knowosad.httpresponsechecker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
public class HttpResponseCheckerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(HttpResponseCheckerApplication.class, args);

		// TODO - SET FILE PATH !!!
		String pathToFileWithUrls = "C:/Users/KacperNowosad/Downloads/http-response-checker/links_to_read.txt";
		callUrls(pathToFileWithUrls);
	}

	public static void callUrls(String filePath) throws IOException {

		// create sheet
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Http response codes");

		// create header row with two cells
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("URL");
		headerRow.createCell(1).setCellValue("Http response code");
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);

		// read file with urls
		BufferedReader fileReader = getTextFile(filePath);
		String urlFromFile = "";
		int rowNumber = 1;

		// do until we have next line
		// get one url from file and set as urlFromFile
		while ((urlFromFile = fileReader.readLine()) != null){

			// get url protocol (http/https)
			String protocol = urlFromFile.substring(0, 5);

			// call url and get response code
			URL currentUrl = new URL(urlFromFile);
			int responseCode;
			if ("https".equalsIgnoreCase(protocol)){
				HttpsURLConnection httpsConnection = (HttpsURLConnection) currentUrl.openConnection();
				responseCode = httpsConnection.getResponseCode();
			} else {
				HttpURLConnection httpConnection = (HttpURLConnection) currentUrl.openConnection();
				responseCode = httpConnection.getResponseCode();
			}

			// add calls values to the new row
			Row newRow = sheet.createRow(rowNumber);
			newRow.createCell(0).setCellValue(String.valueOf(currentUrl));
			newRow.createCell(1).setCellValue(String.valueOf(responseCode));

//			System.out.println(rowNumber + " : " + currentUrl + " - " + responseCode);
			rowNumber++;
		}

		System.out.println("Checked " + (rowNumber - 1) + " urls");
		saveToFile(workbook);
	}

	private static BufferedReader getTextFile (String path) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		DataInputStream dis = new DataInputStream(fis);
		return new BufferedReader(new InputStreamReader(dis));
	}

	private static void saveToFile(Workbook workbook) throws IOException {
		// the default file location is the project folder ( .../http-response-checker )
		FileOutputStream fileOut = new FileOutputStream("http_response_codes.xlsx");
		workbook.write(fileOut);
		fileOut.close();
	}
}
