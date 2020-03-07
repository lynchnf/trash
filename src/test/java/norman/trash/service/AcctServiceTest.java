package norman.trash.service;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Acct;
import norman.trash.domain.repository.AcctRepository;
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
import java.util.List;
import java.util.Optional;

import static norman.trash.domain.AcctType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class AcctServiceTest {
    private Acct acct1;
    private Acct acct2;
    private Acct acct3;

    @TestConfiguration
    static class AcctServiceTestConfiguration {
        @Bean
        public AcctService getAcctService() {
            return new AcctService();
        }
    }

    @Autowired
    private AcctService acctService;
    @MockBean
    private AcctRepository repository;

    @Before
    public void setUp() throws Exception {
        acct1 = FakeDataUtil.buildAcct(1L, "Metropolitan Power and Light", BILL, null);
        acct2 = FakeDataUtil.buildAcct(2L, "Iridium Card", CC, "11111");
        acct3 = FakeDataUtil.buildAcct(3L, "First National Bank", CHECKING, "11111");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByNameAndType() {
        Page<Acct> page = new PageImpl<>(Arrays.asList(acct1));
        when(repository.findByNameContainingIgnoreCaseAndType(eq("power"), eq(BILL), any(Pageable.class)))
                .thenReturn(page);

        Page<Acct> accts = acctService.findByNameAndType("power", BILL, Pageable.unpaged());
        assertEquals(1, accts.getSize());
        Acct acct = accts.iterator().next();
        assertEquals(1L, acct.getId().longValue());
    }

    @Test
    public void findByName() {
        Page<Acct> page = new PageImpl<>(Arrays.asList(acct1));
        when(repository.findByNameContainingIgnoreCase(eq("power"), any(Pageable.class))).thenReturn(page);

        Page<Acct> accts = acctService.findByName("power", Pageable.unpaged());
        assertEquals(1, accts.getSize());
        Acct acct = accts.iterator().next();
        assertEquals(1L, acct.getId().longValue());
    }

    @Test
    public void findByType() {
        Page<Acct> page = new PageImpl<>(Arrays.asList(acct1));
        when(repository.findByType(eq(BILL), any(Pageable.class))).thenReturn(page);

        Page<Acct> accts = acctService.findByType(BILL, Pageable.unpaged());
        assertEquals(1, accts.getSize());
        Acct acct = accts.iterator().next();
        assertEquals(1L, acct.getId().longValue());
    }

    @Test
    public void findAllByPage() {
        Page<Acct> page = new PageImpl<>(Arrays.asList(acct2, acct3, acct1));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Acct> accts = acctService.findAll(Pageable.unpaged());
        assertEquals(3, accts.getSize());
    }

    @Test
    public void findAll() {
        when(repository.findAll(Sort.by("name"))).thenReturn(Arrays.asList(acct2, acct3, acct1));

        Iterable<Acct> accts = acctService.findAll();
        Iterator<Acct> iterator = accts.iterator();
        Acct acctA = iterator.next();
        assertEquals(2L, acctA.getId().longValue());
        Acct acctB = iterator.next();
        assertEquals(3L, acctB.getId().longValue());
        Acct acctC = iterator.next();
        assertEquals(1L, acctC.getId().longValue());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void findById() throws NotFoundException {
        when(repository.findById(Long.valueOf(1L))).thenReturn(Optional.of(acct1));

        Acct acct = acctService.findById(Long.valueOf(1L));
        assertEquals("Metropolitan Power and Light", acct.getName());
    }

    @Test
    public void findByIdNotFoundException() {
        Long id = Long.valueOf(9L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            Acct acct = acctService.findById(id);
        } catch (NotFoundException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void save() throws OptimisticLockingException {
        when(repository.save(acct1)).thenReturn(acct1);

        Acct save = acctService.save(acct1);
        assertEquals(1L, save.getId().longValue());
        assertEquals("Metropolitan Power and Light", save.getName());
    }

    @Test
    public void saveOptimisticLockingException() {
        ObjectOptimisticLockingFailureException exception =
                new ObjectOptimisticLockingFailureException("Test", new Exception());
        when(repository.save(acct1)).thenThrow(exception);

        boolean exceptionThrown = false;
        try {
            Acct save = acctService.save(acct1);
        } catch (OptimisticLockingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void findByOfxFid() { // ??
        when(repository.findByOfxFidOrderByName("11111")).thenReturn(Arrays.asList(acct3, acct2));

        List<Acct> accts = acctService.findByOfxFid("11111");
        assertEquals(2, accts.size());
        assertEquals(3L, accts.get(0).getId().longValue());
        assertEquals(2L, accts.get(1).getId().longValue());
    }

    @Test
    public void findByOfxFidNull() { //?
        when(repository.findByOfxFidNullOrderByName()).thenReturn(Arrays.asList(acct1));

        List<Acct> accts = acctService.findByOfxFidNull();
        assertEquals(1, accts.size());
        Acct acct = accts.iterator().next();
        assertEquals(1L, acct.getId().longValue());
    }
}
