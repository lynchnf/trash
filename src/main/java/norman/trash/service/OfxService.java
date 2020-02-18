package norman.trash.service;

import norman.trash.domain.*;
import norman.trash.domain.repository.OfxParseState;
import norman.trash.exception.OfxParseBadStateException;
import norman.trash.exception.OfxParseBadTokenException;
import norman.trash.exception.OfxParseException;
import norman.trash.exception.OfxParseMissingTokenException;
import norman.trash.service.response.OfxAcct;
import norman.trash.service.response.OfxInst;
import norman.trash.service.response.OfxParseResponse;
import norman.trash.service.response.OfxStmtTran;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OfxService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfxService.class);
    private static final String FI = "<FI>";
    private static final String FI_END = "</FI>";
    private static final String ORG = "<ORG>";
    private static final String FID = "<FID>";
    private static final String BANKACCTFROM = "<BANKACCTFROM>";
    private static final String BANKACCTFROM_END = "</BANKACCTFROM>";
    private static final String CCACCTFROM = "<CCACCTFROM>";
    private static final String CCACCTFROM_END = "</CCACCTFROM>";
    private static final String BANKID = "<BANKID>";
    private static final String ACCTID = "<ACCTID>";
    private static final String ACCTTYPE = "<ACCTTYPE>";
    private static final String BANKTRANLIST = "<BANKTRANLIST>";
    private static final String BANKTRANLIST_END = "</BANKTRANLIST>";
    private static final String STMTTRN = "<STMTTRN>";
    private static final String STMTTRN_END = "</STMTTRN>";
    private static final String TRNTYPE = "<TRNTYPE>";
    private static final String DTPOSTED = "<DTPOSTED>";
    private static final String DTUSER = "<DTUSER>";
    private static final String TRNAMT = "<TRNAMT>";
    private static final String FITID = "<FITID>";
    private static final String SIC = "<SIC>";
    private static final String CHECKNUM = "<CHECKNUM>";
    private static final String CORRECTFITID = "<CORRECTFITID>";
    private static final String CORRECTACTION = "<CORRECTACTION>";
    private static final String NAME = "<NAME>";
    private static final String CATEGORY = "<CATEGORY>";
    private static final String MEMO = "<MEMO>";
    private static final DateFormat DF = new SimpleDateFormat("yyyyMMddHHmmss");

    public OfxParseResponse parseDataFile(DataFile dataFile) throws OfxParseException {
        OfxParseResponse response = new OfxParseResponse();
        OfxParseState state = OfxParseState.OFX;
        for (DataLine dataLine : dataFile.getDataLines()) {
            String line = dataLine.getText();
            if (state == OfxParseState.OFX) {
                if (line.contains(FI)) {
                    state = OfxParseState.FI;
                    response.setOfxInst(new OfxInst());
                } else if (line.contains(FI_END)) {
                    badToken(state, line);
                } else if (line.contains(ORG)) {
                    badToken(state, line);
                } else if (line.contains(FID)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM)) {
                    state = OfxParseState.BANKACCTFROM;
                    response.setOfxAcct(new OfxAcct());
                } else if (line.contains(BANKACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM)) {
                    state = OfxParseState.CCACCTFROM;
                    response.setOfxAcct(new OfxAcct());
                } else if (line.contains(CCACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(BANKID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTTYPE)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST)) {
                    state = OfxParseState.BANKTRANLIST;
                } else if (line.contains(BANKTRANLIST_END)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN_END)) {
                    badToken(state, line);
                } else if (line.contains(TRNTYPE)) {
                    badToken(state, line);
                } else if (line.contains(DTPOSTED)) {
                    badToken(state, line);
                } else if (line.contains(DTUSER)) {
                    badToken(state, line);
                } else if (line.contains(TRNAMT)) {
                    badToken(state, line);
                } else if (line.contains(FITID)) {
                    badToken(state, line);
                } else if (line.contains(SIC)) {
                    badToken(state, line);
                } else if (line.contains(CHECKNUM)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTFITID)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTACTION)) {
                    badToken(state, line);
                } else if (line.contains(NAME)) {
                    badToken(state, line);
                } else if (line.contains(CATEGORY)) {
                    badToken(state, line);
                } else if (line.contains(MEMO)) {
                    badToken(state, line);
                }
            } else if (state == OfxParseState.FI) {
                if (line.contains(FI)) {
                    badToken(state, line);
                } else if (line.contains(FI_END)) {
                    state = OfxParseState.OFX;
                } else if (line.contains(ORG)) {
                    String s = StringUtils.substringAfter(line, ORG);
                    response.getOfxInst().setOrganization(s);
                } else if (line.contains(FID)) {
                    String s = StringUtils.substringAfter(line, FID);
                    response.getOfxInst().setFid(s);
                } else if (line.contains(BANKACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(BANKID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTTYPE)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST_END)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN_END)) {
                    badToken(state, line);
                } else if (line.contains(TRNTYPE)) {
                    badToken(state, line);
                } else if (line.contains(DTPOSTED)) {
                    badToken(state, line);
                } else if (line.contains(DTUSER)) {
                    badToken(state, line);
                } else if (line.contains(TRNAMT)) {
                    badToken(state, line);
                } else if (line.contains(FITID)) {
                    badToken(state, line);
                } else if (line.contains(SIC)) {
                    badToken(state, line);
                } else if (line.contains(CHECKNUM)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTFITID)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTACTION)) {
                    badToken(state, line);
                } else if (line.contains(NAME)) {
                    badToken(state, line);
                } else if (line.contains(CATEGORY)) {
                    badToken(state, line);
                } else if (line.contains(MEMO)) {
                    badToken(state, line);
                } else {
                    missingToken(state, line);
                }
            } else if (state == OfxParseState.BANKACCTFROM) {
                if (line.contains(FI)) {
                    badToken(state, line);
                } else if (line.contains(FI_END)) {
                    badToken(state, line);
                } else if (line.contains(ORG)) {
                    badToken(state, line);
                } else if (line.contains(FID)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM_END)) {
                    state = OfxParseState.OFX;
                } else if (line.contains(CCACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(BANKID)) {
                    String s = StringUtils.substringAfter(line, BANKID);
                    response.getOfxAcct().setBankId(s);
                } else if (line.contains(ACCTID)) {
                    String s = StringUtils.substringAfter(line, ACCTID);
                    response.getOfxAcct().setAcctId(s);
                } else if (line.contains(ACCTTYPE)) {
                    String s = StringUtils.substringAfter(line, ACCTTYPE);
                    AcctType acctType = AcctType.valueOf(s);
                    response.getOfxAcct().setType(acctType);
                } else if (line.contains(BANKTRANLIST)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST_END)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN_END)) {
                    badToken(state, line);
                } else if (line.contains(TRNTYPE)) {
                    badToken(state, line);
                } else if (line.contains(DTPOSTED)) {
                    badToken(state, line);
                } else if (line.contains(DTUSER)) {
                    badToken(state, line);
                } else if (line.contains(TRNAMT)) {
                    badToken(state, line);
                } else if (line.contains(FITID)) {
                    badToken(state, line);
                } else if (line.contains(SIC)) {
                    badToken(state, line);
                } else if (line.contains(CHECKNUM)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTFITID)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTACTION)) {
                    badToken(state, line);
                } else if (line.contains(NAME)) {
                    badToken(state, line);
                } else if (line.contains(CATEGORY)) {
                    badToken(state, line);
                } else if (line.contains(MEMO)) {
                    badToken(state, line);
                } else {
                    missingToken(state, line);
                }
            } else if (state == OfxParseState.CCACCTFROM) {
                if (line.contains(FI)) {
                    badToken(state, line);
                } else if (line.contains(FI_END)) {
                    badToken(state, line);
                } else if (line.contains(ORG)) {
                    badToken(state, line);
                } else if (line.contains(FID)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM_END)) {
                    state = OfxParseState.OFX;
                } else if (line.contains(BANKID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTID)) {
                    String s = StringUtils.substringAfter(line, ACCTID);
                    response.getOfxAcct().setAcctId(s);
                } else if (line.contains(ACCTTYPE)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST_END)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN_END)) {
                    badToken(state, line);
                } else if (line.contains(TRNTYPE)) {
                    badToken(state, line);
                } else if (line.contains(DTPOSTED)) {
                    badToken(state, line);
                } else if (line.contains(DTUSER)) {
                    badToken(state, line);
                } else if (line.contains(TRNAMT)) {
                    badToken(state, line);
                } else if (line.contains(FITID)) {
                    badToken(state, line);
                } else if (line.contains(SIC)) {
                    badToken(state, line);
                } else if (line.contains(CHECKNUM)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTFITID)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTACTION)) {
                    badToken(state, line);
                } else if (line.contains(NAME)) {
                    badToken(state, line);
                } else if (line.contains(CATEGORY)) {
                    badToken(state, line);
                } else if (line.contains(MEMO)) {
                    badToken(state, line);
                } else {
                    missingToken(state, line);
                }
            } else if (state == OfxParseState.BANKTRANLIST) {
                if (line.contains(FI)) {
                    badToken(state, line);
                } else if (line.contains(FI_END)) {
                    badToken(state, line);
                } else if (line.contains(ORG)) {
                    badToken(state, line);
                } else if (line.contains(FID)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(BANKID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTTYPE)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST_END)) {
                    state = OfxParseState.OFX;
                } else if (line.contains(STMTTRN)) {
                    state = OfxParseState.STMTTRN;
                    response.getOfxStmtTrans().add(new OfxStmtTran());
                } else if (line.contains(STMTTRN_END)) {
                    badToken(state, line);
                } else if (line.contains(TRNTYPE)) {
                    badToken(state, line);
                } else if (line.contains(DTPOSTED)) {
                    badToken(state, line);
                } else if (line.contains(DTUSER)) {
                    badToken(state, line);
                } else if (line.contains(TRNAMT)) {
                    badToken(state, line);
                } else if (line.contains(FITID)) {
                    badToken(state, line);
                } else if (line.contains(SIC)) {
                    badToken(state, line);
                } else if (line.contains(CHECKNUM)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTFITID)) {
                    badToken(state, line);
                } else if (line.contains(CORRECTACTION)) {
                    badToken(state, line);
                } else if (line.contains(NAME)) {
                    badToken(state, line);
                } else if (line.contains(CATEGORY)) {
                    badToken(state, line);
                } else if (line.contains(MEMO)) {
                    badToken(state, line);
                }
            } else if (state == OfxParseState.STMTTRN) {
                if (line.contains(FI)) {
                    badToken(state, line);
                } else if (line.contains(FI_END)) {
                    badToken(state, line);
                } else if (line.contains(ORG)) {
                    badToken(state, line);
                } else if (line.contains(FID)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(BANKACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM)) {
                    badToken(state, line);
                } else if (line.contains(CCACCTFROM_END)) {
                    badToken(state, line);
                } else if (line.contains(BANKID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTID)) {
                    badToken(state, line);
                } else if (line.contains(ACCTTYPE)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST)) {
                    badToken(state, line);
                } else if (line.contains(BANKTRANLIST_END)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN)) {
                    badToken(state, line);
                } else if (line.contains(STMTTRN_END)) {
                    state = OfxParseState.BANKTRANLIST;
                } else if (line.contains(TRNTYPE)) {
                    String s = StringUtils.substringAfter(line, TRNTYPE);
                    TranType tranType = TranType.valueOf(s);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setType(tranType);
                } else if (line.contains(DTPOSTED)) {
                    String s = StringUtils.substringAfter(line, DTPOSTED);
                    try {
                        Date d = DF.parse(s.substring(0, 14));
                        int idx = response.getOfxStmtTrans().size() - 1;
                        response.getOfxStmtTrans().get(idx).setPostDate(d);
                    } catch (ParseException e) {
                        throw new OfxParseException(LOGGER, "post date", line, e);
                    }
                } else if (line.contains(DTUSER)) {
                    String s = StringUtils.substringAfter(line, DTUSER);
                    try {
                        Date d = DF.parse(s.substring(0, 14));
                        int idx = response.getOfxStmtTrans().size() - 1;
                        response.getOfxStmtTrans().get(idx).setUserDate(d);
                    } catch (ParseException e) {
                        throw new OfxParseException(LOGGER, "user date", line, e);
                    }
                } else if (line.contains(TRNAMT)) {
                    String s = StringUtils.substringAfter(line, TRNAMT);
                    BigDecimal bd = new BigDecimal(s);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setAmount(bd);
                } else if (line.contains(FITID)) {
                    String s = StringUtils.substringAfter(line, FITID);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setFitId(s);
                } else if (line.contains(SIC)) {
                    String s = StringUtils.substringAfter(line, SIC);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setSic(s);
                } else if (line.contains(CHECKNUM)) {
                    String s = StringUtils.substringAfter(line, CHECKNUM);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setCheckNumber(s);
                } else if (line.contains(CORRECTFITID)) {
                    String s = StringUtils.substringAfter(line, CORRECTFITID);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setCorrectFitId(s);
                } else if (line.contains(CORRECTACTION)) {
                    String s = StringUtils.substringAfter(line, CORRECTACTION);
                    CorrectAction correctAction = CorrectAction.valueOf(s);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setCorrectAction(correctAction);
                } else if (line.contains(NAME)) {
                    String s = StringUtils.substringAfter(line, NAME);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setName(s);
                } else if (line.contains(CATEGORY)) {
                    String s = StringUtils.substringAfter(line, CATEGORY);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setCategory(s);
                } else if (line.contains(MEMO)) {
                    String s = StringUtils.substringAfter(line, MEMO);
                    int idx = response.getOfxStmtTrans().size() - 1;
                    response.getOfxStmtTrans().get(idx).setMemo(s);
                } else {
                    missingToken(state, line);
                }
            } else {
                badState(state);
            }
        }
        return response;
    }

    private void badState(OfxParseState state) throws OfxParseBadStateException {
        throw new OfxParseBadStateException(LOGGER, state);
    }

    private void badToken(OfxParseState state, String line) throws OfxParseBadTokenException {
        throw new OfxParseBadTokenException(LOGGER, state, line);
    }

    private void missingToken(OfxParseState state, String line) throws OfxParseMissingTokenException {
        throw new OfxParseMissingTokenException(LOGGER, state, line);
    }
}
