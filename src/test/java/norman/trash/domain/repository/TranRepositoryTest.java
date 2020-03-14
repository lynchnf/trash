package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static norman.trash.domain.AcctType.BILL;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TranRepositoryTest {
    private Acct acct;
    private Stmt stmt;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TranRepository repository;

    @Before
    public void setUp() throws Exception {
        acct = persistAcct("aabcx", BILL, "33333");

        stmt = persistStmt(2020, 1, 1, acct);

        persistTran(2020, 1, 1, stmt);
        persistTran(2020, 2, 3, stmt);
        persistTran(2020, 3, 5, stmt);
        persistTran(2020, 1, 7, stmt);
        persistTran(2020, 2, 8, stmt);
        persistTran(2020, 3, 6, stmt);
        persistTran(2020, 1, 4, stmt);
        persistTran(2020, 2, 2, stmt);
        entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByStmt_Id() {
        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "postDate", "id");
        Page<Tran> tranPage = repository.findByStmt_Id(stmt.getId(), pageable);
        assertEquals(1, tranPage.getNumber()); // page number (zero based)
        assertEquals(5, tranPage.getSize()); // page size
        Sort.Order sortOrder = tranPage.getSort().iterator().next();
        assertEquals("postDate", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, tranPage.getTotalPages()); // number of pages
        assertEquals(8, tranPage.getTotalElements()); // number of items found
        assertTrue(tranPage.hasPrevious()); // any pages before the current page?
        assertFalse(tranPage.hasNext()); // any pages after the current page?

        List<Tran> content = tranPage.getContent();
        assertEquals(3, content.size());
        Date dt1 = Date.from(LocalDate.of(2020, 1, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt1, content.get(0).getPostDate());
        Date dt2 = Date.from(LocalDate.of(2020, 1, 4).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt2, content.get(1).getPostDate());
        Date dt3 = Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt3, content.get(2).getPostDate());
    }

    private Acct persistAcct(String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        return entityManager.persist(acct);
    }

    private Stmt persistStmt(int year, int month, int day, Acct acct) {
        Date closeDate = Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Stmt stmt = new Stmt();
        stmt.setCloseDate(closeDate);
        stmt.setAcct(acct);
        return entityManager.persist(stmt);
    }

    private void persistTran(int year, int month, int day, Stmt stmt) {
        Date closeDate = Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Tran tran = new Tran();
        tran.setPostDate(closeDate);
        tran.setAmount(BigDecimal.TEN);
        tran.setName("foobar");
        tran.setStmt(stmt);
        entityManager.persist(tran);
    }
}
