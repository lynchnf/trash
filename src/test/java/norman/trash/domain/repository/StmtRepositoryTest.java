package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.Stmt;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static norman.trash.domain.AcctType.BILL;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class StmtRepositoryTest {
    private Acct acct;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StmtRepository repository;

    @Before
    public void setUp() throws Exception {
        acct = persistAcct("aabcx", BILL, "33333");

        persistStmt(2020, 1, 1, acct);
        persistStmt(2020, 2, 3, acct);
        persistStmt(2020, 3, 5, acct);
        persistStmt(2020, 1, 7, acct);
        persistStmt(2020, 2, 8, acct);
        persistStmt(2020, 3, 6, acct);
        persistStmt(2020, 1, 4, acct);
        persistStmt(2020, 2, 2, acct);
        entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByAcct_Id() {
        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "closeDate", "id");
        Page<Stmt> stmtPage = repository.findByAcct_Id(acct.getId(), pageable);
        assertEquals(1, stmtPage.getNumber()); // page number (zero based)
        assertEquals(5, stmtPage.getSize()); // page size
        Sort.Order sortOrder = stmtPage.getSort().iterator().next();
        assertEquals("closeDate", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, stmtPage.getTotalPages()); // number of pages
        assertEquals(8, stmtPage.getTotalElements()); // number of items found
        assertTrue(stmtPage.hasPrevious()); // any pages before the current page?
        assertFalse(stmtPage.hasNext()); // any pages after the current page?

        List<Stmt> content = stmtPage.getContent();
        assertEquals(3, content.size());
        Date dt1 = Date.from(LocalDate.of(2020, 1, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt1, content.get(0).getCloseDate());
        Date dt2 = Date.from(LocalDate.of(2020, 1, 4).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt2, content.get(1).getCloseDate());
        Date dt3 = Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt3, content.get(2).getCloseDate());
    }

    @Test
    public void findByAcct_IdAndCloseDate() {
        Date closeDate = Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Stmt> stmts = repository.findByAcct_IdAndCloseDate(acct.getId(), closeDate);

        assertEquals(1, stmts.size());
        Date dt = Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt, stmts.get(0).getCloseDate());
    }

    private Acct persistAcct(String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        return entityManager.persist(acct);
    }

    private void persistStmt(int year, int month, int day, Acct acct) {
        Date closeDate = Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Stmt stmt = new Stmt();
        stmt.setCloseDate(closeDate);
        stmt.setAcct(acct);
        entityManager.persist(stmt);
    }
}
