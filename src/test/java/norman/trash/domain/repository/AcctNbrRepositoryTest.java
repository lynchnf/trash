package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctNbr;
import norman.trash.domain.AcctType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static norman.trash.domain.AcctType.BILL;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AcctNbrRepositoryTest {
    private Acct acct;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AcctNbrRepository repository;

    @Before
    public void setUp() throws Exception {
        acct = persistAcct("aabcx", BILL, "33333");

        persistAcctNbr("9911", acct);
        persistAcctNbr("9923", acct);
        persistAcctNbr("9935", acct);
        persistAcctNbr("9917", acct);
        persistAcctNbr("9928", acct);
        persistAcctNbr("9936", acct);
        persistAcctNbr("9914", acct);
        persistAcctNbr("9922", acct);
        entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByAcct_OfxFidAndNumber() {
        List<AcctNbr> acctNbrs = repository.findByAcct_OfxFidAndNumber("33333", "9928");

        assertEquals(1, acctNbrs.size());
        assertEquals("33333", acctNbrs.get(0).getAcct().getOfxFid());
        assertEquals("9928", acctNbrs.get(0).getNumber());
    }

    private Acct persistAcct(String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        return entityManager.persist(acct);
    }

    private void persistAcctNbr(String number, Acct acct) {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setNumber(number);
        acctNbr.setAcct(acct);
        Date effDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        acctNbr.setEffDate(effDate);
        entityManager.persist(acctNbr);
    }
}
