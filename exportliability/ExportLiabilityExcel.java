package com.nordea.cfpt.report.excel.exportliability;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.nordea.cfpt.core.customer.Customer;
import com.nordea.cfpt.core.user.User;
import com.nordea.cfpt.report.excel.BaseExcel;
import com.nordea.cfpt.report.pdf.report360.liability.current.amortizationchart.AmortizationChartProvider;
import com.nordea.cfpt.report.pdf.report360.liability.current.amortizationchart.AmortizationChartProviderBuilder;
import com.nordea.cfpt.view.culture.TranslationsProvider;

public class ExportLiabilityExcel
        extends BaseExcel<LiabilityExcelDataProvider> {

    private List<Customer> customerList;

    private User thisUser;

    public ExportLiabilityExcel(List<Customer> customerList,
            TranslationsProvider translator, User thisUser) {
        super(translator);

        setRowHeight(186);
        this.customerList = customerList;
        this.thisUser = thisUser;
    }

    @Override
    protected void prepardData() {
        dataList = new ArrayList<>();
        for (Customer customer : customerList) {
            LiabilityExcelDataProvider data = new LiabilityExcelDataProvider(
                    customer, translator, thisUser);
            dataList.add(data);
        }
    }

    @Override
    protected void initColumns() {

        createColumn(0).setTitle("LOAN_RPT__CUST_ID_COL").setWidth(12)
                .setDisplayValue(p -> p.getCustomerId());
        createColumn(1).setTitle("LOAN_RPT__CUST_INFO_COL").setWidth(30)
                .setDisplayValue(p -> p.getCustomerInfo());
        createColumn(2).setTitle("LOAN_RPT__BISNODE_COL").setWidth(17)
                .setDisplayValue(p -> p.getBidnodeData());
        createColumn(3).setTitle("LOAN_RPT__LOAN_COL").setWidth(16)
                .setDisplayValue(p -> p.getLoanData());
        createColumn(4).setTitle("LOAN_RPT__HEDGE_RATIO_COL").setWidth(16)
                .setDisplayValue(p -> p.getHedgeRatio());
        createColumn(5).setTitle("LOAN_RPT__HEDGE_COL").setWidth(16)
                .setDisplayValue(p -> p.getHedgeData());
        createColumn(6).setTitle("LOAN_RPT__GRAPH_COL").setWidth(71)
                .setDisplayChart(p -> getAmortizationProvider()
                        .getAmortizationChart(p.getLiabilityData(),
                                p.getLiabilityData().getEffectiveDate()));

    }

    private AmortizationChartProvider getAmortizationProvider() {
        java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN,
                10);
        return new AmortizationChartProviderBuilder().setComparisonChart(false)
                .setTranslator(translator).setJfreeLabelFontSmall(font)
                .setJfreeLabelFont(font).setLegendFont(font)
                .setDefaultChartBackgroundColor(new Color(251, 217, 202))
                .build();
    }

}
