/*
package com.ezc.aragenPR.webapp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

//import com.ezc.aragenPR.webapp.model.EzQmisForecastData;
//import com.ezc.aragenPR.webapp.persistance.dao.QmisForecastDataRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class EzExcelUtil {
	@Autowired
	private QmisForecastDataRepository qmisForecastDataRepository;

	public String getCellValue(String fileExt,Iterator cellIter)
	{
		String celValue = "";
		if(cellIter.hasNext())
		{
			if("xlsx".equals(fileExt))
			{
				XSSFCell myCellX = (XSSFCell) cellIter.next();
				if(myCellX.getCellTypeEnum() == CellType.NUMERIC) 
				{
					celValue=new BigDecimal(myCellX.getNumericCellValue())+"";
					//log.debug("Integer:::"+celValue);
				}
				else if(myCellX.getCellTypeEnum() == CellType.STRING) 
				{
					celValue=myCellX.getStringCellValue()+"";
					//log.debug("String:::"+celValue);
				}
				else
				{
					celValue=myCellX.getStringCellValue()+"";
				}
			}
			else
			{
				HSSFCell myCellH = (HSSFCell) cellIter.next();
				if(myCellH.getCellTypeEnum() == CellType.NUMERIC) 
				{
					celValue=myCellH.getNumericCellValue()+"";
					//log.debug("Integer:::"+celValue);
				}
				else if(myCellH.getCellTypeEnum() == CellType.STRING) 
				{
					celValue=myCellH.getStringCellValue()+"";
					//log.debug("String:::"+celValue);
				}else{
					celValue=myCellH.getStringCellValue()+"";
				}
			}
			if(celValue == null || "null".equals(celValue.trim()) || "".equals(celValue.trim()))
				celValue ="";
		}
		return celValue;
	}	

	public List<Object[]> readExcel(InputStream uploadedInputStream,String fileName)
	{
		DecimalFormat format = new DecimalFormat("0.###");
		XSSFWorkbook wbX 	= null;
		HSSFWorkbook wbH 	= null;
		HSSFSheet mySheetH	= null;
		XSSFSheet mySheetX 	= null;
		Iterator rowIter	= null;
		int ExtPos 	= fileName.lastIndexOf(".");
		String fileExt	= fileName.substring(ExtPos+1,fileName.length());
		List<Object[]> retObjArr = new ArrayList<Object[]>(); 
		log.debug("fileExt::::"+fileExt);
		try {
			if("xlsx".equals(fileExt))
			{
				wbX 		= new XSSFWorkbook(uploadedInputStream);
				mySheetX	= wbX.getSheetAt(0);
				rowIter 	= mySheetX.rowIterator();
			}
			else
			{
				wbH 		= new HSSFWorkbook(uploadedInputStream);
				mySheetH 	= wbH.getSheetAt(0);
				rowIter 	= mySheetH.rowIterator();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int firstRow=0;
		int noOfCols = 0;
		try{
			HSSFRow myRowH 	= null;
			XSSFRow myRowX 	= null;
			while (rowIter.hasNext()) 
			{
				if("xlsx".equals(fileExt))
					myRowX 	= (XSSFRow) rowIter.next();
				else
					myRowH 	= (HSSFRow) rowIter.next();
				Iterator cellIter = null;
				if("xlsx".equals(fileExt))
					cellIter = myRowX.cellIterator();
				else
					cellIter = myRowH.cellIterator();
				if(firstRow==0)
				{
					
					boolean endLoop = true;
					while(endLoop)
					{
						String colName = getCellValue(fileExt,cellIter);
						if(!"".equals(colName))
						{
							//retObj.addColumn(colName.toUpperCase());
							noOfCols++;
						}else{
							endLoop=false;
						}
					}
					
					
					firstRow++;
				}
				else
				{
					Object[] rowObj = new Object[noOfCols];
					for(int i=0;i<noOfCols;i++)
					{
						String colValue = "";//getCellValue(fileExt,cellIter);
						if("xlsx".equals(fileExt))
						{
							XSSFCell myCellX = (XSSFCell) myRowX.getCell(i);
							if(myCellX != null)
							{
								if(myCellX.getCellTypeEnum() == CellType.NUMERIC) 
								{
									colValue=format.format(myCellX.getNumericCellValue())+"";
									log.debug("Integer:::"+colValue);
								}
								else if(myCellX.getCellTypeEnum() == CellType.STRING) 
								{
									colValue=myCellX.getStringCellValue()+"";
									log.debug("String:::"+colValue);
								}
								else
								{
									colValue=myCellX.getStringCellValue()+"";
								}
							}
							
						}
						else
						{
							HSSFCell myCellH = (HSSFCell) myRowH.getCell(i);
							if(myCellH != null)
							{
								if(myCellH.getCellTypeEnum() == CellType.NUMERIC) 
								{
									colValue=format.format(myCellH.getNumericCellValue())+"";
									log.debug("Integer:::"+colValue);
								}
								else if(myCellH.getCellTypeEnum() == CellType.STRING) 
								{
									colValue=myCellH.getStringCellValue()+"";
									log.debug("String:::"+colValue);
								}else{
									colValue=myCellH.getStringCellValue()+"";
								}
							}
						}
						//retObj.setFieldValue(i, colValue);
						rowObj[i]=colValue;
					}
					*/
/*
					boolean endLoop = true;
					int colIndex = 0;
					while(endLoop)
					{
						String colValue = getCellValue(fileExt,cellIter);
						if(!"".equals(colValue))
						{
							retObj.setFieldValue(colIndex, colValue);
							colIndex++;
						}else{
							endLoop=false;
						}
					}
					*//*

					
					retObjArr.add(rowObj);


				}	
			}
		}
		catch(Exception Ex)
		{
			log.debug(Ex+":::Exception:::");

		}
		finally
		{
			try
			{
				uploadedInputStream.close();
			}
			catch(Exception ex){}
		} 
		return retObjArr;
	}
	public StreamingResponseBody writeExcel(String [] headerCols,List<Object[]> objList,String sheetName)
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int rowNum = 0;
		log.debug("Creating excel");
		Row headerRow = sheet.createRow(rowNum++);
		int headerColNum = 0;
		String [] docArray = headerCols;
		CellStyle headerStyle = workbook.createCellStyle();//Create style
	    Font font = workbook.createFont();//Create font
	    font.setBold(true);//Make font bold
	    headerStyle.setFont(font);//set it to bold
		for(int i=0;i<docArray.length;i++)
		{
			Cell headerCell= headerRow.createCell(headerColNum++);
			headerCell.setCellValue(docArray[i]);
			headerCell.setCellStyle(headerStyle);
		}
		if(objList != null)
		{
			for (Object[] obj : objList) {
				Row row = sheet.createRow(rowNum++);
				int colNum = 0;
				for (Object field : obj) {
					Cell cell = row.createCell(colNum++);
					if (field instanceof String) {
						cell.setCellValue((String) field);
					} else if (field instanceof Integer) {
						cell.setCellValue((Integer) field);
					}else if (field instanceof Double) {
						cell.setCellValue((Double) field);
					}else if (field instanceof Long) {
						cell.setCellValue((Long) field);
					}
				}
			}
		}
		
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final byte[] bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

		
		return outputStream1 -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            	outputStream1.write(data, 0, nRead);
            }
            inputStream.close();
        };
	}
	public StreamingResponseBody generateExcelTemplate(List<String> headers1, List<String> headers2, List<String> months, String sheetName, HttpServletResponse response, String vendorName, String type) throws IOException
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		CellStyle headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		headerStyle.setFont(font);

		CellStyle mainHeaderStyle = workbook.createCellStyle();
		mainHeaderStyle.setFont(font);
		mainHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
		mainHeaderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		CellStyle monthStyle = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		monthStyle.setDataFormat(format.getFormat("@"));
		monthStyle.setAlignment(CellStyle.ALIGN_CENTER);
		monthStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		monthStyle.setFont(font);


		int length = headers1.size();
		Row row0 = sheet.createRow(0);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, length));
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(vendorName + " - Yearly Budget Data" + " - " + type);
		cell0.setCellStyle(mainHeaderStyle);


		Row row1 = sheet.createRow(1);
		Cell cell00 = row1.createCell(0);
		cell00.setCellValue("PARAM/MONTH");
		cell00.setCellStyle(headerStyle);
		for (int i = 0; i < headers1.size(); i++) {
			Cell cell = row1.createCell(i + 1);
			cell.setCellValue(headers1.get(i));
			cell.setCellStyle(headerStyle);
		}

		Row row2 = sheet.createRow(2);
		Cell cell10 = row2.createCell(0);
		cell10.setCellValue("UoM");
		cell10.setCellStyle(headerStyle);
		for (int i = 0; i < headers2.size(); i++) {
			Cell cell = row2.createCell(i + 1);
			cell.setCellValue(headers2.get(i));
			cell.setCellStyle(headerStyle);
		}

		int rowNum = 3;
		for (String month : months) {
			Row row = sheet.createRow(rowNum++);
			Cell cell = row.createCell(0);
			cell.setCellStyle(monthStyle);
			cell.setCellValue(month);
		}

		for (int i = 0; i < headers1.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			workbook.write(response.getOutputStream());
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final byte[] bytes = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);


		return outputStream1 -> {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStream1.write(data, 0, nRead);
			}
			inputStream.close();
		};
	}
	public void readExcelAndSaveToDb(MultipartFile file, String vendorId) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet sheet = workbook.getSheetAt(0);

		String mergedHeader = sheet.getRow(0).getCell(0).getStringCellValue();
		String[] parts = mergedHeader.split(" - ");
		String vendor = parts[0].trim();
		String type = parts.length > 2 ? parts[2].trim() : "";

		List<String> headers1 = new ArrayList<>();
		List<String> headers2 = new ArrayList<>();

		Row headerRow1 = sheet.getRow(1);
		Row headerRow2 = sheet.getRow(2);
		for (int i = 1; i < headerRow1.getLastCellNum(); i++) {
			headers1.add(headerRow1.getCell(i).getStringCellValue());
			headers2.add(headerRow2.getCell(i).getStringCellValue());
		}


		for (int i = 3; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null || row.getCell(0) == null) continue;
			String[] monthYear = row.getCell(0).getStringCellValue().split("-");
			String month = monthYear[0];
			int Mnth = Month.from(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH).parse(month)).getValue();
			int year = Integer.parseInt(monthYear[1]);
			for (int j = 1; j < row.getLastCellNum(); j++) {
				Cell valueCell = row.getCell(j);
				if (valueCell == null) continue;
				double value = valueCell.getNumericCellValue();
				System.out.println(vendor + " - " + type + " - " + year + " - " + month + " - " + headers1.get(j - 1) + " - " + headers2.get(j - 1) + " - " + value);
				Optional<EzQmisForecastData> existingData =
						qmisForecastDataRepository.findByEqfdYearAndEqfdMonthAndEqfdType1AndEqfdType3AndEqfdVendor(
								year, Mnth, type, headers1.get(j - 1), vendor
						);

				EzQmisForecastData ezQmisForecastData;

				if (existingData.isPresent()) {
					ezQmisForecastData = existingData.get();
					ezQmisForecastData.setEqfdForecastValue((float) value);
				} else {
					ezQmisForecastData = new EzQmisForecastData();
					ezQmisForecastData.setEqfdType1(type);
					ezQmisForecastData.setEqfdYear(year);
					ezQmisForecastData.setEqfdMonth(Month.from(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH).parse(month)).getValue());
					ezQmisForecastData.setEqfdType3(headers1.get(j - 1));
					ezQmisForecastData.setEqfdVendor(vendorId);
					ezQmisForecastData.setEqfdForecastValue((float) value);
				}

				qmisForecastDataRepository.save(ezQmisForecastData);

			}
		}
		workbook.close();
	}

}
*/
