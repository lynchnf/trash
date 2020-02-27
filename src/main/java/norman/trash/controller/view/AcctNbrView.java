package norman.trash.controller.view;

import norman.trash.domain.AcctNbr;

import java.util.Date;

public class AcctNbrView {
    private Long id;
    private String number;
    private Date effDate;
    private Long acctId;
    private String acctName;

    public AcctNbrView(AcctNbr acctNbr) {
        id = acctNbr.getId();
        number = acctNbr.getNumber();
        effDate = acctNbr.getEffDate();
        acctId = acctNbr.getAcct().getId();
        acctName = acctNbr.getAcct().getName();
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Date getEffDate() {
        return effDate;
    }

    public Long getAcctId() {
        return acctId;
    }

    public String getAcctName() {
        return acctName;
    }
}
