package com.example;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

public class MessagesXlsxView extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) model.get("messages");
        
        Sheet sheet = workbook.createSheet("Recent messages");
        
        // create header
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("Name");
        row.createCell(2).setCellValue("Text");
        row.createCell(3).setCellValue("RemoteAddr");
        row.createCell(4).setCellValue("CreatedAt");
        
        // create body
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i=0; i<messages.size(); i++) {
            Message message = messages.get(i);
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(message.getId());
            row.createCell(1).setCellValue(message.getName());
            row.createCell(2).setCellValue(message.getText());
            row.createCell(3).setCellValue(message.getRemoteAddr());
            row.createCell(4).setCellValue(dateFormatter.format(message.getCreatedAt()));
        }
        
        // enable auto filter
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 4));
        
        // adjust column width
        for (int i=0; i<5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}
