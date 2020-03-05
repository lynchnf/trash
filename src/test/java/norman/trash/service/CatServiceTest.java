package norman.trash.service;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Cat;
import norman.trash.domain.Pattern;
import norman.trash.domain.repository.CatRepository;
import norman.trash.exception.NotFoundException;
import norman.trash.exception.OptimisticLockingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CatServiceTest {
    private Cat cat1;
    private Cat cat2;
    private Cat cat3;
    private Pattern pattern11;
    private Pattern pattern12;
    private Pattern pattern13;

    @TestConfiguration
    static class CatServiceTestConfiguration {
        @Bean
        public CatService getCatService() {
            return new CatService();
        }
    }

    @Autowired
    private CatService catService;
    @MockBean
    private CatRepository repository;
    @MockBean
    private PatternService patternService;

    @Before
    public void setUp() throws Exception {
        cat1 = FakeDataUtil.buildCat(1L, "Utilities");
        cat2 = FakeDataUtil.buildCat(2L, "Groceries");
        cat3 = FakeDataUtil.buildCat(3L, "Mortgage");
        pattern11 = FakeDataUtil.buildPattern(11L, cat2);
        pattern11.setRegex(".*Food.*");
        pattern12 = FakeDataUtil.buildPattern(12L, cat1);
        pattern12.setRegex(".*Power.*");
        pattern13 = FakeDataUtil.buildPattern(13L, cat3);
        pattern13.setRegex(".*Pow.*");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByName() {
        Page<Cat> page = new PageImpl<>(Arrays.asList(cat1));
        when(repository.findByNameContainingIgnoreCase(eq("util"), any(Pageable.class))).thenReturn(page);

        Page<Cat> cats = catService.findByName("util", Pageable.unpaged());
        assertEquals(1, cats.getSize());
        Cat cat = cats.iterator().next();
        assertEquals(1L, cat.getId().longValue());
    }

    @Test
    public void findAllByPage() {
        Page<Cat> page = new PageImpl<>(Arrays.asList(cat2, cat3, cat1));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Cat> cats = catService.findAll(Pageable.unpaged());
        assertEquals(3, cats.getSize());
    }

    @Test
    public void findAll() {
        when(repository.findAll(Sort.by("name"))).thenReturn(Arrays.asList(cat2, cat3, cat1));

        Iterable<Cat> cats = catService.findAll();
        Iterator<Cat> iterator = cats.iterator();
        Cat catA = iterator.next();
        assertEquals(2L, catA.getId().longValue());
        Cat catB = iterator.next();
        assertEquals(3L, catB.getId().longValue());
        Cat catC = iterator.next();
        assertEquals(1L, catC.getId().longValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void findById() throws NotFoundException {
        when(repository.findById(Long.valueOf(1L))).thenReturn(Optional.of(cat1));

        Cat cat = catService.findById(Long.valueOf(1L));
        assertEquals("Utilities", cat.getName());
    }

    @Test
    public void findByIdNotFoundException() {
        Long id = Long.valueOf(9L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            Cat cat = catService.findById(id);
        } catch (NotFoundException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void save() throws OptimisticLockingException {
        when(repository.save(cat1)).thenReturn(cat1);

        Cat save = catService.save(cat1);
        assertEquals(1L, save.getId().longValue());
        assertEquals("Utilities", save.getName());
    }

    @Test
    public void saveOptimisticLockingException() {
        ObjectOptimisticLockingFailureException exception =
                new ObjectOptimisticLockingFailureException("Test", new Exception());
        when(repository.save(cat1)).thenThrow(exception);

        boolean exceptionThrown = false;
        try {
            Cat save = catService.save(cat1);
        } catch (OptimisticLockingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void findByPattern() {
        when(patternService.findAll()).thenReturn(Arrays.asList(pattern11, pattern12, pattern13));

        Cat cat = catService.findByPattern("Metropolitan Power and Light");
        assertEquals(1L, cat.getId().longValue());
    }

    @Test
    public void findByPatternNotFound() {
        when(patternService.findAll()).thenReturn(Arrays.asList(pattern11, pattern12, pattern13));

        Cat cat = catService.findByPattern("First National Bank");
        assertNull(cat);
    }
}
