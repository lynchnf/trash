package norman.trash.controller.view;

import norman.trash.domain.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataTranMatchForm {
    private Long dataFileId;
    private String originalFilename;
    @DateTimeFormat(pattern = "M/d/yyyy H:m:s")
    private Date uploadTimestamp;
    private DataFileStatus status;
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
        originalFilename = dataFile.getOriginalFilename();
        uploadTimestamp = dataFile.getUploadTimestamp();
        status = dataFile.getStatus();
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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
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
