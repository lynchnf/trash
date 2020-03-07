package norman.trash.service;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Acct;
import norman.trash.domain.Stmt;
import norman.trash.domain.repository.StmtRepository;
import norman.trash.exception.MultipleOptimisticLockingException;
import norman.trash.exception.NotFoundException;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static norman.trash.domain.AcctType.BILL;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class StmtServiceTest {
    private Acct acct1;
    private Stmt stmt11;
    private Stmt stmt12;
    private Stmt stmt13;

    @TestConfiguration
    static class StmtServiceTestConfiguration {
        @Bean
        public StmtService getStmtService() {
            return new StmtService();
        }
    }

    @Autowired
    private StmtService stmtService;
    @MockBean
    private StmtRepository repository;

    @Before
    public void setUp() throws Exception {
        acct1 = FakeDataUtil.buildAcct(1L, "Metropolitan Power and Light", BILL, null);
        stmt11 = FakeDataUtil.buildBeginStmt(11L, LocalDate.of(2020, 1, 1), acct1);
        stmt12 = FakeDataUtil.buildStmt(12L, LocalDate.of(2020, 2, 1), acct1);
        stmt13 = FakeDataUtil.buildCurrentStmt(13L, acct1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findByAcctId() {
        Page<Stmt> page = new PageImpl<>(Arrays.asList(stmt11, stmt12, stmt13));
        when(repository.findByAcct_Id(eq(Long.valueOf(1L)), any(Pageable.class))).thenReturn(page);

        Page<Stmt> stmts = stmtService.findByAcctId(1L, Pageable.unpaged());
        assertEquals(3, stmts.getSize());
    }

    @Test
    public void findById() throws NotFoundException {
        when(repository.findById(Long.valueOf(12L))).thenReturn(Optional.of(stmt12));

        Stmt stmt = stmtService.findById(Long.valueOf(12L));
        Date dt = Date.from(LocalDate.of(2020, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt, stmt.getCloseDate());
    }

    @Test
    public void findByIdNotFoundException() {
        Long id = Long.valueOf(9L);
        when(repository.findById(id)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            Stmt stmt = stmtService.findById(id);
        } catch (NotFoundException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void findByAcctIdAndCloseDate() {
        Date closeDate = Date.from(LocalDate.of(2020, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        when(repository.findByAcct_IdAndCloseDate(1L, closeDate)).thenReturn(Arrays.asList(stmt12));

        Stmt stmt = stmtService.findByAcctIdAndCloseDate(1L, closeDate);
        Date dt = Date.from(LocalDate.of(2020, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        assertEquals(dt, stmt.getCloseDate());
    }

    @Test
    public void saveAll() throws MultipleOptimisticLockingException {
        stmtService.saveAll(Arrays.asList(stmt12, stmt13));
    }

    @Test
    public void saveAllMultipleOptimisticLockingException() {
        when(repository.saveAll(Arrays.asList(stmt12, stmt13)))
                .thenThrow(new ObjectOptimisticLockingFailureException("test", new Exception()));

        boolean exceptionThrown = false;
        try {
            stmtService.saveAll(Arrays.asList(stmt12, stmt13));
        } catch (MultipleOptimisticLockingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
