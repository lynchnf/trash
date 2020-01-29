package norman.trash.controller.view;

import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class StmtView {
    private Long id;
    private String acctName;
    private Date closeDate;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public StmtView(Stmt stmt) {
        id = stmt.getId();
        acctName = stmt.getAcct().getName();
        closeDate = stmt.getCloseDate();

        debitAmount = BigDecimal.ZERO;
        for (Tran tran : stmt.getDebitTrans()) {
            debitAmount = debitAmount.subtract(tran.getAmount());
        }
        creditAmount = BigDecimal.ZERO;
        for (Tran tran : stmt.getCreditTrans()) {
            creditAmount = creditAmount.add(tran.getAmount());
        }
    }

    public Long getId() {
        return id;
    }

    public String getAcctName() {
        return acctName;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }
}
