package com.nordea.cfpt.report.excel;

import java.util.function.Function;

import org.jfree.chart.JFreeChart;

public class ExcelColDefinition<T> {
    private String title;

    private int width;

    private int id;

    private Function<T, String> displayValue;

    private Function<T, JFreeChart> displayChart;

    public ExcelColDefinition() {
    }

    public String getTitle() {
        return title;
    }

    public ExcelColDefinition<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ExcelColDefinition<T> setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getId() {
        return id;
    }

    public ExcelColDefinition<T> setId(int id) {
        this.id = id;
        return this;
    }

    public Function<T, String> getDisplayValue() {
        return displayValue;
    }

    public ExcelColDefinition<T> setDisplayValue(
            Function<T, String> displayValue) {
        this.displayValue = displayValue;
        return this;
    }

    public Function<T, JFreeChart> getDisplayChart() {
        return displayChart;
    }

    public ExcelColDefinition<T> setDisplayChart(
            Function<T, JFreeChart> displayChart) {
        this.displayChart = displayChart;
        return this;
    }

}