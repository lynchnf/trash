package norman.trash.controller.view;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;

import java.util.Date;

public class DataFileView {
    private Long id;
    private String originalFilename;
    private String contentType;
    private Long size;
    private Date uploadTimestamp;
    private DataFileStatus status;
    private String ofxOrganization;
    private String ofxFid;
    private String ofxBankId;
    private String ofxAcctId;
    private AcctType ofxType;
    private Long acctId;
    private String acctName;

    public DataFileView(DataFile dataFile) {
        id = dataFile.getId();
        originalFilename = dataFile.getOriginalFilename();
        contentType = dataFile.getContentType();
        size = dataFile.getSize();
        uploadTimestamp = dataFile.getUploadTimestamp();
        status = dataFile.getStatus();
        ofxOrganization = dataFile.getOfxOrganization();
        ofxFid = dataFile.getOfxFid();
        ofxBankId = dataFile.getOfxBankId();
        ofxAcctId = dataFile.getOfxAcctId();
        ofxType = dataFile.getOfxType();
        Acct acct = dataFile.getAcct();
        if (acct != null) {
            acctId = acct.getId();
            acctName = acct.getName();
        }
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public String getOfxOrganization() {
        return ofxOrganization;
    }

    public String getOfxFid() {
        return ofxFid;
    }

    public String getOfxBankId() {
        return ofxBankId;
    }

    public String getOfxAcctId() {
        return ofxAcctId;
    }

    public AcctType getOfxType() {
        return ofxType;
    }

    public Long getAcctId() {
        return acctId;
    }

    public String getAcctName() {
        return acctName;
    }
}
