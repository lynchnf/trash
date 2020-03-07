package norman.trash;

import norman.trash.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class FakeDataUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeDataUtil.class);

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");
    }

    public static Cat buildCat(long id, String name) {
        Cat cat = new Cat();
        cat.setId(id);
        cat.setName(name);
        return cat;
    }

    public static Pattern buildPattern(long id, int seq, String regex, Cat cat) {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setSeq(seq);
        pattern.setRegex(regex);
        pattern.setCat(cat);
        cat.getPatterns().add(pattern);
        return pattern;
    }

    public static Acct buildAcct(long id, String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        return acct;
    }

    public static Stmt buildStmt(long id, LocalDate closeDate, Acct acct) {
        Stmt stmt = new Stmt();
        stmt.setId(id);
        Date date = Date.from(closeDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        stmt.setCloseDate(date);
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);
        return stmt;
    }

    public static Stmt buildBeginStmt(long id, LocalDate beginDate, Acct acct) {
        return buildStmt(id, beginDate, acct);
    }

    public static Stmt buildCurrentStmt(long id, Acct acct) {
        return buildStmt(id, LocalDate.of(9999, 12, 31), acct);
    }

    private static Tran buildTranImpl(long id, Date postDate, BigDecimal amount, Stmt stmt) {
        Tran tran = new Tran();
        tran.setId(id);
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setStmt(stmt);
        stmt.getTrans().add(tran);
        return tran;
    }

    public static Tran buildTran(long id, LocalDate postDate, BigDecimal amount, Stmt stmt) {
        Date date = Date.from(postDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return buildTranImpl(id, date, amount, stmt);
    }

    public static Tran buildBeginTran(long id, BigDecimal beginBalance, Stmt beginStmt) {
        return buildTranImpl(id, beginStmt.getCloseDate(), beginBalance, beginStmt);
    }
}
