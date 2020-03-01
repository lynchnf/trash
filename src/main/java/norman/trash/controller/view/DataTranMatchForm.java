package norman.trash.controller.view;

import norman.trash.domain.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataTranMatchForm {
    private Long dataFileId;
    private Integer dataFileVersion;
    private String originalFilename;
    private String contentType;
    private Long size;
    @DateTimeFormat(pattern = "M/d/yyyy H:m:s")
    private Date uploadTimestamp;
    private DataFileStatus status;
    private String ofxOrganization;
    private String ofxFid;
    private String ofxBankId;
    private String ofxAcctId;
    private AcctType ofxType;
    private Long acctId;
    private String acctName;
    private AcctType acctType;
    private Long stmtId;
    @Valid
    private List<DataTranMatchTarget> dataTranMatchTargets = new ArrayList<>();
    @Valid
    private List<TranMatchPayload> tranMatchPayloads = new ArrayList<>();

    public DataTranMatchForm() {
    }

    public DataTranMatchForm(DataFile dataFile, Stmt stmt) {
        dataFileId = dataFile.getId();
        dataFileVersion = dataFile.getVersion();
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
        acctId = stmt.getAcct().getId();
        acctName = stmt.getAcct().getName();
        acctType = stmt.getAcct().getType();
        stmtId = stmt.getId();

        List<String> alreadyMatchedFitIds = new ArrayList<>();
        for (Tran tran : stmt.getTrans()) {
            String ofxFitId = tran.getOfxFitId();
            if (ofxFitId == null) {
                TranMatchPayload payload = new TranMatchPayload(tran);
                tranMatchPayloads.add(payload);
            } else {
                alreadyMatchedFitIds.add(ofxFitId);
            }
        }

        for (DataTran dataTran : dataFile.getDataTrans()) {
            if (!alreadyMatchedFitIds.contains(dataTran.getOfxFitId())) {
                DataTranMatchTarget target = new DataTranMatchTarget(dataTran);
                dataTranMatchTargets.add(target);
            }
        }
    }

    public DataFile toDataFile() {
        DataFile dataFile = new DataFile();
        dataFile.setId(dataFileId);
        dataFile.setVersion(dataFileVersion);
        dataFile.setOriginalFilename(originalFilename);
        dataFile.setContentType(contentType);
        dataFile.setSize(size);
        dataFile.setUploadTimestamp(uploadTimestamp);
        dataFile.setStatus(DataFileStatus.TR_MATCHED);
        dataFile.setOfxOrganization(ofxOrganization);
        dataFile.setOfxFid(ofxFid);
        dataFile.setOfxBankId(ofxBankId);
        dataFile.setOfxAcctId(ofxAcctId);
        dataFile.setOfxType(ofxType);
        dataFile.setAcct(new Acct());
        dataFile.getAcct().setId(acctId);
        return dataFile;
    }

    public List<Tran> toTrans() {
        List<Tran> trans = new ArrayList<>();
        for (DataTranMatchTarget target : dataTranMatchTargets) {
            Tran tran = target.toTran();
            tran.setStmt(new Stmt());
            tran.getStmt().setId(stmtId);
            trans.add(tran);
        }
        return trans;
    }

    public Long getDataFileId() {
        return dataFileId;
    }

    public void setDataFileId(Long dataFileId) {
        this.dataFileId = dataFileId;
    }

    public Integer getDataFileVersion() {
        return dataFileVersion;
    }

    public void setDataFileVersion(Integer dataFileVersion) {
        this.dataFileVersion = dataFileVersion;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
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

    public String getOfxAcctId() {
        return ofxAcctId;
    }

    public void setOfxAcctId(String ofxAcctId) {
        this.ofxAcctId = ofxAcctId;
    }

    public AcctType getOfxType() {
        return ofxType;
    }

    public void setOfxType(AcctType ofxType) {
        this.ofxType = ofxType;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public AcctType getAcctType() {
        return acctType;
    }

    public void setAcctType(AcctType acctType) {
        this.acctType = acctType;
    }

    public Long getStmtId() {
        return stmtId;
    }

    public void setStmtId(Long stmtId) {
        this.stmtId = stmtId;
    }

    public List<DataTranMatchTarget> getDataTranMatchTargets() {
        return dataTranMatchTargets;
    }

    public void setDataTranMatchTargets(List<DataTranMatchTarget> dataTranMatchTargets) {
        this.dataTranMatchTargets = dataTranMatchTargets;
    }

    public List<TranMatchPayload> getTranMatchPayloads() {
        return tranMatchPayloads;
    }

    public void setTranMatchPayloads(List<TranMatchPayload> tranMatchPayloads) {
        this.tranMatchPayloads = tranMatchPayloads;
    }
}
