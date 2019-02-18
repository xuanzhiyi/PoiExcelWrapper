package com.nordea.cfpt.report.excel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.nordea.cfpt.view.culture.TranslationsProvider;

public abstract class BaseExcel<T> {
    private Workbook workbook;

    protected List<T> dataList;

    protected Logger logger = Logger.getLogger(BaseExcel.class);

    protected TranslationsProvider translator;

    protected List<ExcelColDefinition<T>> columnList;

    private int height = 20;

    public BaseExcel(TranslationsProvider translator) {
        this.translator = translator;

        columnList = new ArrayList<>();
    }

    protected abstract void initColumns();

    protected abstract void prepardData();

    protected void setRowHeight(int height) {
        this.height = height;
    }

    protected void insertChart(Sheet sheet, int rownum, int column, int height,
            int width, JFreeChart chart) {

        try {

            ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(chart_out, chart, width, height);

            int my_picture_id = sheet.getWorkbook().addPicture(
                    chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);

            chart_out.close();

            @SuppressWarnings("rawtypes")
            Drawing drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = sheet.getWorkbook().getCreationHelper()
                    .createClientAnchor();

            anchor.setCol1(column);
            anchor.setRow1(rownum - 1);

            Picture pict = drawing.createPicture(anchor, my_picture_id);

            pict.resize();

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    protected ExcelColDefinition<T> createColumn(int id) {
        ExcelColDefinition<T> col = new ExcelColDefinition<T>().setId(id);
        columnList.add(col);
        return col;
    }

    public Workbook createExcel() {

        prepardData();

        initColumns();

        workbook = new XSSFWorkbook();
        // create a new sheet
        Sheet sheet = workbook.createSheet();
        // declare a row object reference
        Row row = null;
        // declare a cell object reference
        Cell cell = null;

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);

        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle cellboldStyle = workbook.createCellStyle();
        cellboldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellboldStyle.setFont(font);

        try {

            // titles
            row = sheet.createRow(0);
            row.setHeight((short) (35 * 15));

            for (ExcelColDefinition<T> column : columnList) {

                cell = row.createCell(column.getId());
                cell.setCellValue(translator.getString(column.getTitle()));
                sheet.setColumnWidth(column.getId(),
                        column.getWidth() * 256 + 200);
                cell.setCellStyle(cellboldStyle);
            }

            // data
            int rownum = 1;
            for (T data : dataList) {

                row = sheet.createRow(rownum++);
                row.setHeight((short) (height * 15));

                for (ExcelColDefinition<T> column : columnList) {
                    if (column.getDisplayValue() != null) {
                        cell = row.createCell(column.getId());
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(column.getDisplayValue().apply(data));
                    }
                    if (column.getDisplayChart() != null)
                        insertChart(sheet, rownum, column.getId(), height,
                                column.getWidth() * 7,
                                column.getDisplayChart().apply(data));
                }

            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        return workbook;
    }
}
