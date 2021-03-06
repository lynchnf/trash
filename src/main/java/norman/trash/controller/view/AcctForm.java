package norman.trash.controller.view;

import norman.trash.controller.view.validation.AfterDateIfValueChange;
import norman.trash.controller.view.validation.NotNullIfCondition;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctNbr;
import norman.trash.domain.AcctType;
import norman.trash.domain.DataFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@AfterDateIfValueChange(newDate = "effDate", oldDate = "oldEffDate", newString = "number", oldString = "oldNumber",
        message = "If Account Number changed, new Effective Date must be after the old Effective Date.")
@NotNullIfCondition(fieldName = "beginningBalance", conditionField = "newEntity",
        message = "If new Account, Beginning Balance may not be blank.")
public class AcctForm {
    private Long id;
    private Integer version = 0;
    @NotBlank(message = "Account Name may not be blank.")
    @Size(max = 50, message = "Account Name may not be over {max} characters long.")
    private String name;
    @NotNull(message = "Account Type may not be blank.")
    private AcctType type;
    @Size(max = 50, message = "Address Name may not be over {max} characters long.")
    private String addressName;
    @Size(max = 50, message = "Address Line 1 may not be over {max} characters long.")
    private String address1;
    @Size(max = 50, message = "Address Line 2 may not be over {max} characters long.")
    private String address2;
    @Size(max = 50, message = "City may not be over {max} characters long.")
    private String city;
    @Size(max = 2, message = "State may not be over {max} characters long.")
    private String state;
    @Size(max = 10, message = "Zip Code may not be over {max} characters long.")
    private String zipCode;
    @Size(max = 20, message = "Phone Number may not be over {max} characters long.")
    private String phoneNumber;
    @PositiveOrZero(message = "Credit Limit may not be negative.")
    @Digits(integer = 7, fraction = 2,
            message = "Credit Limit value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal creditLimit;
    private String ofxOrganization;
    private String ofxFid;
    private String ofxBankId;
    private boolean newEntity = true;
    //
    @NotBlank(message = "Account Number may not be blank.")
    @Size(max = 50, message = "Account Number may not be over {max} characters long.")
    private String number;
    private String oldNumber;
    @NotNull(message = "Effective Date may not be blank.")
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date effDate;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date oldEffDate;
    //
    @Digits(integer = 7, fraction = 2,
            message = "Beginning Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal beginningBalance;
    //
    private Long dataFileId;

    public AcctForm() {
    }

    public AcctForm(Acct acct, DataFile dataFile) {
        if (acct != null) {
            id = acct.getId();
            version = acct.getVersion();
            name = acct.getName();
            type = acct.getType();
            addressName = acct.getAddressName();
            address1 = acct.getAddress1();
            address2 = acct.getAddress2();
            city = acct.getCity();
            state = acct.getState();
            zipCode = acct.getZipCode();
            phoneNumber = acct.getPhoneNumber();
            creditLimit = acct.getCreditLimit();
            ofxOrganization = acct.getOfxOrganization();
            ofxFid = acct.getOfxFid();
            ofxBankId = acct.getOfxBankId();
            newEntity = false;

            // Get current account number and effective date.
            List<AcctNbr> acctNbrs = acct.getAcctNbrs();
            acctNbrs.sort(Comparator.comparing(AcctNbr::getEffDate));
            Collections.reverse(acctNbrs);
            if (acctNbrs.size() >= 1) {
                number = acctNbrs.get(0).getNumber();
                oldNumber = acctNbrs.get(0).getNumber();
                effDate = acctNbrs.get(0).getEffDate();
                oldEffDate = acctNbrs.get(0).getEffDate();
            }
        }

        if (dataFile != null) {
            dataFileId = dataFile.getId();
            if (dataFile.getOfxType() != null) {
                type = dataFile.getOfxType();
            }
            ofxOrganization = dataFile.getOfxOrganization();
            ofxFid = dataFile.getOfxFid();
            ofxBankId = dataFile.getOfxBankId();
            number = dataFile.getOfxAcctId();
        }
    }

    public Acct toAcct() {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setVersion(version);
        acct.setName(StringUtils.trimToNull(name));
        acct.setType(type);
        acct.setAddressName(StringUtils.trimToNull(addressName));
        acct.setAddress1(StringUtils.trimToNull(address1));
        acct.setAddress2(StringUtils.trimToNull(address2));
        acct.setCity(StringUtils.trimToNull(city));
        acct.setState(StringUtils.trimToNull(state));
        acct.setZipCode(StringUtils.trimToNull(zipCode));
        acct.setPhoneNumber(StringUtils.trimToNull(phoneNumber));
        acct.setCreditLimit(creditLimit);
        acct.setOfxOrganization(StringUtils.trimToNull(ofxOrganization));
        acct.setOfxFid(StringUtils.trimToNull(ofxFid));
        acct.setOfxBankId(StringUtils.trimToNull(ofxBankId));
        return acct;
    }

    public AcctNbr toAcctNbr() {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setNumber(StringUtils.trimToNull(number));
        acctNbr.setEffDate(effDate);
        return acctNbr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getOfxOrganization() {
        return ofxOrganization;
    }

    public void setOfxOrganization(String ofxOrganization) {
        this.ofxOrganization = ofxOrganization;
    }

    public String getOfxFid() {
        return ofxFid;
    }

    public void setOfxFid(String ofxFid) {
        this.ofxFid = ofxFid;
    }

    public String getOfxBankId() {
        return ofxBankId;
    }

    public void setOfxBankId(String ofxBankId) {
        this.ofxBankId = ofxBankId;
    }

    public boolean isNewEntity() {
        return newEntity;
    }

    public void setNewEntity(boolean newEntity) {
        this.newEntity = newEntity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public Date getOldEffDate() {
        return oldEffDate;
    }

    public void setOldEffDate(Date oldEffDate) {
        this.oldEffDate = oldEffDate;
    }

    public BigDecimal getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(BigDecimal beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public Long getDataFileId() {
        return dataFileId;
    }

    public void setDataFileId(Long dataFileId) {
        this.dataFileId = dataFileId;
    }
}
