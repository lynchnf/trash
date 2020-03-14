package norman.trash.domain.repository;

import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;
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
public class PatternRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private PatternRepository repository;

    @Before
    public void setUp() throws Exception {
        Cat cat = persistCat("cat");

        persistPattern(20, "aabcx", cat);
        persistPattern(21, "cdefx", cat);
        persistPattern(22, "eghix", cat);
        persistPattern(23, "gAbcx", cat);
        persistPattern(24, "iDefx", cat);
        persistPattern(10, "kGhix", cat);
        persistPattern(11, "maBcx", cat);
        persistPattern(12, "odEfx", cat);
        persistPattern(13, "qgHix", cat);
        persistPattern(14, "sABcx", cat);
        persistPattern(15, "uDEfx", cat);
        persistPattern(16, "wGHix", cat);
        persistPattern(17, "xabCx", cat);
        persistPattern(18, "vdeFx", cat);
        persistPattern(19, "tghIx", cat);
        persistPattern(1, "rAbCx", cat);
        persistPattern(2, "pDeFx", cat);
        persistPattern(3, "nGhIx", cat);
        persistPattern(4, "laBCx", cat);
        persistPattern(5, "jdEFx", cat);
        persistPattern(6, "hgHIx", cat);
        persistPattern(7, "fABCx", cat);
        persistPattern(8, "dDEFx", cat);
        persistPattern(9, "bGHIx", cat);
        entityManager.flush();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByRegexContainingIgnoreCase() {
        PageRequest pageable = PageRequest.of(1, 5, Sort.Direction.DESC, "regex", "id");
        Page<Pattern> patternPage = repository.findByRegexContainingIgnoreCase("def", pageable);
        assertEquals(1, patternPage.getNumber()); // page number (zero based)
        assertEquals(5, patternPage.getSize()); // page size
        Sort.Order sortOrder = patternPage.getSort().iterator().next();
        assertEquals("regex", sortOrder.getProperty()); // sort column
        assertEquals(Sort.Direction.DESC, sortOrder.getDirection()); // sort direction
        assertEquals(2, patternPage.getTotalPages()); // number of pages
        assertEquals(8, patternPage.getTotalElements()); // number of items found
        assertTrue(patternPage.hasPrevious()); // any pages before the current page?
        assertFalse(patternPage.hasNext()); // any pages after the current page?

        List<Pattern> content = patternPage.getContent();
        assertEquals(3, content.size());
        assertEquals("iDefx", content.get(0).getRegex());
        assertEquals("dDEFx", content.get(1).getRegex());
        assertEquals("cdefx", content.get(2).getRegex());
    }

    private Cat persistCat(String name) {
        Cat cat = new Cat();
        cat.setName(name);
        return entityManager.persist(cat);
    }

    private void persistPattern(int seq, String regex, Cat cat) {
        Pattern pattern = new Pattern();
        pattern.setSeq(seq);
        pattern.setRegex(regex);
        pattern.setCat(cat);
        entityManager.persist(pattern);
    }
}
