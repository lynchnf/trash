package norman.trash.controller;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Acct;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.service.CatService;
import norman.trash.service.StmtService;
import norman.trash.service.TranService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static norman.trash.controller.view.TranStatus.MANUAL;
import static norman.trash.domain.AcctType.BILL;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(StmtController.class)
public class StmtControllerTest {
    private Acct acct1;
    private Stmt stmt11;
    private Stmt stmt12;
    private Stmt stmt13;
    private Tran tran111;
    private Tran tran121;
    private Tran tran122;
    private Tran tran123;
    private Tran tran131;
    private Tran tran132;
    private Tran tran133;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StmtService stmtService;
    @MockBean
    private TranService tranService;
    @MockBean
    private CatService catService;

    @Before
    public void setUp() throws Exception {
        acct1 = FakeDataUtil.buildAcct(1L, "Metropolitan Power and Light", BILL, null);
        stmt11 = FakeDataUtil.buildBeginStmt(11L, LocalDate.of(2020, 1, 1), acct1);
        stmt12 = FakeDataUtil.buildStmt(12L, LocalDate.of(2020, 2, 1), acct1);
        stmt13 = FakeDataUtil.buildCurrentStmt(13L, acct1);
        tran111 = FakeDataUtil.buildBeginTran(111L, BigDecimal.ZERO, stmt11);
        tran121 = FakeDataUtil
                .buildTran(121L, MANUAL, LocalDate.of(2020, 1, 10), BigDecimal.valueOf(-12345, 2), "abc", null, stmt12);
        tran122 = FakeDataUtil
                .buildTran(122L, MANUAL, LocalDate.of(2020, 1, 20), BigDecimal.valueOf(8230, 2), "abd", null, stmt12);
        tran123 = FakeDataUtil
                .buildTran(123L, MANUAL, LocalDate.of(2020, 1, 30), BigDecimal.valueOf(4115, 2), "abe", null, stmt12);
        tran131 = FakeDataUtil
                .buildTran(131L, MANUAL, LocalDate.of(2020, 2, 10), BigDecimal.valueOf(-5432, 2), "abf", null, stmt13);
        tran132 = FakeDataUtil
                .buildTran(132L, MANUAL, LocalDate.of(2020, 2, 20), BigDecimal.valueOf(5432, 2), "abg", null, stmt13);
        tran133 = FakeDataUtil
                .buildTran(133L, MANUAL, LocalDate.of(2020, 3, 10), BigDecimal.valueOf(-5678, 2), "abh", null, stmt13);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadStmtView() throws Exception {
        when(stmtService.findById(12L)).thenReturn(stmt12);

        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "postDate", "id");
        List<Tran> content = Arrays.asList(tran123, tran122, tran121);
        Page<Tran> page = new PageImpl<>(content, pageable, content.size());
        when(tranService.findByStmtId(eq(Long.valueOf(12L)), any(Pageable.class))).thenReturn(page);

        // @formatter:off
        mockMvc.perform(get("/stmt").param("id", "12"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<p class=\"form-control-plaintext\">2/1/2020</p>")))
                .andExpect(content().string(containsString("<td>¤41.15</td>")))
                .andExpect(content().string(containsString("<td>¤82.30</td>")))
                .andExpect(content().string(containsString("<td>-¤123.45</td>")));
        // @formatter:on
    }

    @Test
    public void loadStmtReconcile() throws Exception {
        when(stmtService.findById(13L)).thenReturn(stmt13);

        // @formatter:off
        mockMvc.perform(get("/stmtReconcile").param("id", "13"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"stmtReconcileRows0.amount\" name=\"stmtReconcileRows[0].amount\" value=\"-54.32\"/>")))
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"stmtReconcileRows1.amount\" name=\"stmtReconcileRows[1].amount\" value=\"54.32\"/>")))
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"stmtReconcileRows2.amount\" name=\"stmtReconcileRows[2].amount\" value=\"-56.78\"/>")));
        // @formatter:on
    }

    @Test
    public void processStmtReconcile() {
    }

    @Test
    public void loadCatsDropDown() {
    }
}
