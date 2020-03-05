package norman.trash.domain.repository;

import norman.trash.domain.Cat;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CatRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CatRepository repository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByNameContainingIgnoreCase() {
        persistCat("aabcx");
        persistCat("cdefx");
        persistCat("eghix");
        persistCat("gAbcx");
        persistCat("iDefx");
        persistCat("kGhix");
        persistCat("maBcx");
        persistCat("odEfx");
        persistCat("qgHix");
        persistCat("sABcx");
        persistCat("uDEfx");
        persistCat("wGHix");
        persistCat("xabCx");
        persistCat("vdeFx");
        persistCat("tghIx");
        persistCat("rAbCx");
        persistCat("pDeFx");
        persistCat("nGhIx");
        persistCat("laBCx");
        persistCat("jdEFx");
        persistCat("hgHIx");
        persistCat("fABCx");
        persistCat("dDEFx");
        persistCat("bGHIx");
        entityManager.flush();

        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "name", "id");
        Page<Cat> catPage = repository.findByNameContainingIgnoreCase("def", pageable);
        assertEquals(1, catPage.getNumber()); // page number (zero based)
        assertEquals(5, catPage.getSize()); // page size
        Sort.Order sortOrder = catPage.getSort().iterator().next();
        assertEquals("name", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, catPage.getTotalPages()); // number of pages
        assertEquals(8, catPage.getTotalElements()); // number of items found
        assertTrue(catPage.hasPrevious()); // any pages before the current page?
        assertFalse(catPage.hasNext()); // any pages after the current page?

        List<Cat> content = catPage.getContent();
        assertEquals(3, content.size());
        assertEquals("iDefx", content.get(0).getName());
        assertEquals("dDEFx", content.get(1).getName());
        assertEquals("cdefx", content.get(2).getName());
    }

    private void persistCat(String name) {
        Cat cat = new Cat();
        cat.setName(name);
        entityManager.persist(cat);
    }
}
