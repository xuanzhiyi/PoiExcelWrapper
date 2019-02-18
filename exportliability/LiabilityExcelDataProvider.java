package com.nordea.cfpt.report.excel.exportliability;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.nordea.cfpt.Services;
import com.nordea.cfpt.core.customer.Customer;
import com.nordea.cfpt.core.financialstatement.FinancialStatement;
import com.nordea.cfpt.core.hedge.Hedge;
import com.nordea.cfpt.core.holding.Holdings;
import com.nordea.cfpt.core.loan.Loan;
import com.nordea.cfpt.core.user.User;
import com.nordea.cfpt.util.LocalizedFormat;
import com.nordea.cfpt.view.culture.TranslationsProvider;
import com.nordea.cfpt.view.lite360.request.LiabilityController;
import com.nordea.cfpt.view.lite360.request.liability.LiabilityData;

public class LiabilityExcelDataProvider {
    private Customer customer;

    private LiabilityData liabilityData;

    private TranslationsProvider translator;

    private FinancialStatement bisnode;

    private User thisUser;

    private LocalizedFormat lft;

    public LiabilityExcelDataProvider(Customer customer,
            TranslationsProvider translator, User thisUser) {

        this.customer = customer;
        this.translator = translator;
        this.thisUser = thisUser;

        lft = thisUser.getLocalizedFormat();

        liabilityData = getCalculatedLiabilityDataByCustomer();

        bisnode = getFinancialStatement();
    }

    public Customer getCustomer() {
        return customer;
    }

    public LiabilityData getLiabilityData() {
        return liabilityData;
    }

    private FinancialStatement getFinancialStatement() {
        List<FinancialStatement> descOrderedFinancialStatements = Services
                .getFinancialStatementsManager().getLatestFinancialStatements(
                        customer.getCompanyRegIdForBisnode(), new Date(), 1);
        if (descOrderedFinancialStatements.size() > 0)
            return descOrderedFinancialStatements.get(0);
        else
            return null;
    }

    private LiabilityData getCalculatedLiabilityDataByCustomer() {
        Holdings holdings = Services.getHoldingsManager()
                .getCustomerHoldings(customer.getId(), thisUser);

        List<Loan> loanList = holdings.getLoans();
        List<Hedge> hedgeList = Services.getHedgeManager()
                .getInfIRSwapsByCust(customer);

        LiabilityData liabilityData = new LiabilityData(null, loanList,
                LiabilityController.getEnabledHedgeList(hedgeList), new Date(),
                customer.getDefaultBaseCcy(), null);
        liabilityData.calculate();
        return liabilityData;
    }

    public String getCustomerId() {
        return customer.getId();
    }

    public String getCustomerInfo() {
        return customer.getFullName() + "\n " + customer.getSrmName();
    }

    public String getBidnodeData() {
        if (bisnode == null)
            return translator.getString("LOAN_RPT__BISNODE_MISSING");

        return translator.getString("LOAN_RPT__BISNODE_TOTAL_DEBTS") + ": "
                + lft.getStringFromNumber(bisnode.getTotalDebts(), 0);
    }

    public String getLoanData() {
        if (liabilityData.getLiabilitySummary().getNumOfLoans() == 0)
            return translator.getString("LOAN_RPT__NO_LOAN");

        return translator.getString("LOAN_RPT__LOAN_TOTAL") + ": "
                + lft.getStringFromNumber(
                        liabilityData.getLiabilitySummary().getLoanTotalNow(),
                        0)
                + "\n" + translator.getString("LOAN_RPT__LOAN_AVG_RATE") + ": "
                + lft.getStringFromPercent(liabilityData.getLiabilitySummary()
                        .getmWeightedAvgIRRate());
    }

    public String getHedgeData() {
        if (liabilityData.getLiabilitySummary().getNumOfHedges() == 0)
            return translator.getString("LOAN_RPT__NO_HEDGE");

        return translator.getString("LOAN_RPT__HEDGE_NEXT_MATURITY") + ": "
                + lft.getStringFromDate(liabilityData.getLiabilitySummary()
                        .getNextHedgeMaturityDate());
    }

    public String getHedgeRatio() {
        if (liabilityData.getLiabilitySummary().getNumOfLoans() == 0)
            return translator.getString("LOAN_RPT__NO_LOAN");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            BigDecimal hedgeRatio = liabilityData.getLiabilitySummary()
                    .getHedgeRatio(i);
            sb.append(i + "y: " + lft.getStringFromPercent(hedgeRatio) + "\n");
        }

        return sb.toString();
    }
}
