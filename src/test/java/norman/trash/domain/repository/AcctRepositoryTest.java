package norman.trash.domain.repository;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
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

import java.util.List;

import static norman.trash.domain.AcctType.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AcctRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AcctRepository repository;

    @Before
    public void setUp() throws Exception {
        persistAcct("aabcx", BILL, "33333");
        persistAcct("cdefx", BILL, "33333");
        persistAcct("eghix", BILL, "33333");
        persistAcct("gAbcx", BILL, "44444");
        persistAcct("iDefx", BILL, "44444");
        persistAcct("kGhix", BILL, "44444");
        persistAcct("maBcx", BILL, "55555");
        persistAcct("odEfx", BILL, "55555");
        persistAcct("qgHix", CC, "55555");
        persistAcct("sABcx", CC, "66666");
        persistAcct("uDEfx", CC, "66666");
        persistAcct("wGHix", CC, "66666");
        persistAcct("xabCx", CC, "77777");
        persistAcct("vdeFx", CC, "77777");
        persistAcct("tghIx", CC, "77777");
        persistAcct("rAbCx", CC, "88888");
        persistAcct("pDeFx", CHECKING, "88888");
        persistAcct("nGhIx", CHECKING, "88888");
        persistAcct("laBCx", CHECKING, "99999");
        persistAcct("jdEFx", CHECKING, "99999");
        persistAcct("hgHIx", CHECKING, "99999");
        persistAcct("fABCx", CHECKING, null);
        persistAcct("dDEFx", CHECKING, null);
        persistAcct("bGHIx", CHECKING, null);
        entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByNameContainingIgnoreCaseAndType() {
        PageRequest pageable = PageRequest.of(0, 50, Sort.Direction.DESC, "name", "id");
        Page<Acct> acctPage = repository.findByNameContainingIgnoreCaseAndType("def", CC, pageable);
        assertEquals(0, acctPage.getNumber()); // page number (zero based)
        assertEquals(50, acctPage.getSize()); // page size
        Sort.Order sortOrder = acctPage.getSort().iterator().next();
        assertEquals("name", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(1, acctPage.getTotalPages()); // number of pages
        assertEquals(2, acctPage.getTotalElements()); // number of items found
        assertFalse(acctPage.hasPrevious()); // any pages before the current page?
        assertFalse(acctPage.hasNext()); // any pages after the current page?

        List<Acct> content = acctPage.getContent();
        assertEquals(2, content.size());
        assertEquals("vdeFx", content.get(0).getName());
        assertEquals("uDEfx", content.get(1).getName());
    }

    @Test
    public void findByNameContainingIgnoreCase() {
        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "name", "id");
        Page<Acct> acctPage = repository.findByNameContainingIgnoreCase("def", pageable);
        assertEquals(1, acctPage.getNumber()); // page number (zero based)
        assertEquals(5, acctPage.getSize()); // page size
        Sort.Order sortOrder = acctPage.getSort().iterator().next();
        assertEquals("name", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, acctPage.getTotalPages()); // number of pages
        assertEquals(8, acctPage.getTotalElements()); // number of items found
        assertTrue(acctPage.hasPrevious()); // any pages before the current page?
        assertFalse(acctPage.hasNext()); // any pages after the current page?

        List<Acct> content = acctPage.getContent();
        assertEquals(3, content.size());
        assertEquals("iDefx", content.get(0).getName());
        assertEquals("dDEFx", content.get(1).getName());
        assertEquals("cdefx", content.get(2).getName());
    }

    @Test
    public void findByType() {
        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "name", "id");
        Page<Acct> acctPage = repository.findByType(CC, pageable);
        assertEquals(1, acctPage.getNumber()); // page number (zero based)
        assertEquals(5, acctPage.getSize()); // page size
        Sort.Order sortOrder = acctPage.getSort().iterator().next();
        assertEquals("name", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, acctPage.getTotalPages()); // number of pages
        assertEquals(8, acctPage.getTotalElements()); // number of items found
        assertTrue(acctPage.hasPrevious()); // any pages before the current page?
        assertFalse(acctPage.hasNext()); // any pages after the current page?

        List<Acct> content = acctPage.getContent();
        assertEquals(3, content.size());
        assertEquals("sABcx", content.get(0).getName());
        assertEquals("rAbCx", content.get(1).getName());
        assertEquals("qgHix", content.get(2).getName());
    }

    @Test
    public void findByOfxFidOrderByName() {
        List<Acct> accts = repository.findByOfxFidOrderByName("44444");
        assertEquals(3, accts.size());
        assertEquals("gAbcx", accts.get(0).getName());
        assertEquals("iDefx", accts.get(1).getName());
        assertEquals("kGhix", accts.get(2).getName());
    }

    @Test
    public void findByOfxFidNullOrderByName() {
        List<Acct> accts = repository.findByOfxFidNullOrderByName();
        assertEquals(3, accts.size());
        assertEquals("bGHIx", accts.get(0).getName());
        assertEquals("dDEFx", accts.get(1).getName());
        assertEquals("fABCx", accts.get(2).getName());
    }

    private void persistAcct(String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        entityManager.persist(acct);
    }
}
