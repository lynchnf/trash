package norman.trash.service.response;

import java.util.ArrayList;
import java.util.List;

public class OfxParseResponse {
    private OfxInst ofxInst;
    private OfxAcct ofxAcct;
    private List<OfxStmtTran> ofxStmtTrans = new ArrayList<>();

    public OfxInst getOfxInst() {
        return ofxInst;
    }

    public void setOfxInst(OfxInst ofxInst) {
        this.ofxInst = ofxInst;
    }

    public OfxAcct getOfxAcct() {
        return ofxAcct;
    }

    public void setOfxAcct(OfxAcct ofxAcct) {
        this.ofxAcct = ofxAcct;
    }

    public List<OfxStmtTran> getOfxStmtTrans() {
        return ofxStmtTrans;
    }

    public void setOfxStmtTrans(List<OfxStmtTran> ofxStmtTrans) {
        this.ofxStmtTrans = ofxStmtTrans;
    }
}