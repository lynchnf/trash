package norman.trash.controller.view;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.DataFile;
import norman.trash.domain.DataFileStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataAcctMatchView {
    private Long id;
    private String originalFilename;
    private Date uploadTimestamp;
    private DataFileStatus status;
    private String ofxOrganization;
    private String ofxFid;
    private String ofxBankId;
    private String ofxAcctId;
    private AcctType ofxType;
    private List<AcctView> sameFidAccts = new ArrayList<>();
    private List<AcctView> noFidAccts = new ArrayList<>();

    public DataAcctMatchView(DataFile dataFile, List<Acct> sameFidAccts, List<Acct> noFidAccts) {
        id = dataFile.getId();
        originalFilename = dataFile.getOriginalFilename();
        uploadTimestamp = dataFile.getUploadTimestamp();
        status = dataFile.getStatus();
        ofxOrganization = dataFile.getOfxOrganization();
        ofxFid = dataFile.getOfxFid();
        ofxBankId = dataFile.getOfxBankId();
        ofxAcctId = dataFile.getOfxAcctId();
        ofxType = dataFile.getOfxType();
        for (Acct acct : sameFidAccts) {
            this.sameFidAccts.add(new AcctView(acct));
        }
        for (Acct acct : noFidAccts) {
            this.noFidAccts.add(new AcctView(acct));
        }
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
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

    public List<AcctView> getSameFidAccts() {
        return sameFidAccts;
    }

    public List<AcctView> getNoFidAccts() {
        return noFidAccts;
    }
}
