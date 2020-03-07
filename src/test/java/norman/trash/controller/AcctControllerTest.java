package norman.trash.controller;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Acct;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.exception.NotFoundException;
import norman.trash.service.AcctService;
import norman.trash.service.DataFileService;
import norman.trash.service.StmtService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static norman.trash.domain.AcctType.*;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AcctController.class)
public class AcctControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcctControllerTest.class);
    private Acct acct1;
    private Acct acct2;
    private Acct acct3;
    private Stmt stmt11;
    private Stmt stmt12;
    private Stmt stmt13;
    private Stmt stmt14;
    private Tran tran111;
    private Tran tran121;
    private Tran tran122;
    private Tran tran123;
    private Tran tran131;
    private Tran tran132;
    private Tran tran141;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AcctService acctService;
    @MockBean
    private StmtService stmtService;
    @MockBean
    private DataFileService dataFileService;

    @Before
    public void setUp() throws Exception {
        acct1 = FakeDataUtil.buildAcct(1L, "Metropolitan Power and Light", BILL, null);
        acct2 = FakeDataUtil.buildAcct(2L, "Iridium Card", CC, "11111");
        acct3 = FakeDataUtil.buildAcct(3L, "First National Bank", CHECKING, "11111");

        stmt11 = FakeDataUtil.buildBeginStmt(11L, LocalDate.of(2020, 1, 1), acct1);
        stmt12 = FakeDataUtil.buildStmt(12L, LocalDate.of(2020, 2, 1), acct1);
        stmt13 = FakeDataUtil.buildStmt(13L, LocalDate.of(2020, 3, 1), acct1);
        stmt14 = FakeDataUtil.buildCurrentStmt(14L, acct1);

        tran111 = FakeDataUtil.buildBeginTran(111L, BigDecimal.ZERO, stmt11);
        tran121 = FakeDataUtil.buildTran(121L, LocalDate.of(2020, 1, 10), BigDecimal.valueOf(-12345, 2), stmt12);
        tran122 = FakeDataUtil.buildTran(122L, LocalDate.of(2020, 1, 20), BigDecimal.valueOf(8230, 2), stmt12);
        tran123 = FakeDataUtil.buildTran(123L, LocalDate.of(2020, 1, 30), BigDecimal.valueOf(4115, 2), stmt12);
        tran131 = FakeDataUtil.buildTran(131L, LocalDate.of(2020, 2, 10), BigDecimal.valueOf(-5432, 2), stmt13);
        tran132 = FakeDataUtil.buildTran(132L, LocalDate.of(2020, 2, 20), BigDecimal.valueOf(5432, 2), stmt13);
        tran141 = FakeDataUtil.buildTran(141L, LocalDate.of(2020, 3, 10), BigDecimal.valueOf(-5678, 2), stmt14);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadAcctList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "name", "id");
        List<Acct> content = Arrays.asList(acct3, acct2, acct1);
        Page<Acct> page = new PageImpl<>(content, pageable, 3L);
        when(acctService.findAll(any(Pageable.class))).thenReturn(page);

        // @formatter:off
        mockMvc.perform(get("/acctList"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<td>First National Bank</td>")))
                .andExpect(content().string(containsString("<td>Iridium Card</td>")))
                .andExpect(content().string(containsString("<td>Metropolitan Power and Light</td>")));
        // @formatter:on
    }

    @Test
    public void loadAcctView() throws Exception {
        when(acctService.findById(1L)).thenReturn(acct1);
        Pageable pageable = PageRequest.of(0, 12, Sort.Direction.DESC, "closeDate", "id");
        List<Stmt> content = Arrays.asList(stmt14, stmt13, stmt12, stmt11);
        Page<Stmt> page = new PageImpl<>(content, pageable, 4L);
        when(stmtService.findByAcctId(eq(Long.valueOf(1L)), any(Pageable.class))).thenReturn(page);

        // @formatter:off
        mockMvc.perform(get("/acct").param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<p class=\"form-control-plaintext\">Metropolitan Power and Light</p>")))
                .andExpect(content().string(containsString("<td>12/31/9999</td>")))
                .andExpect(content().string(containsString("<td>3/1/2020</td>")))
                .andExpect(content().string(containsString("<td>2/1/2020</td>")))
                .andExpect(content().string(containsString("<td>1/1/2020</td>")));
        // @formatter:on
    }

    @Test
    public void loadAcctViewNotFound() throws Exception {
        when(acctService.findById(9L)).thenThrow(new NotFoundException(LOGGER, "Account", 9L));

        // @formatter:off
        mockMvc.perform(get("/acct").param("id", "9"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("errorMessage","Account was not found for id=9."))
                .andExpect(redirectedUrl("/acctList"));
        // @formatter:on
    }

    @Test
    public void loadAcctEdit() throws Exception {
        when(acctService.findById(1L)).thenReturn(acct1);

        // @formatter:off
        mockMvc.perform(get("/acctEdit").param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"id\" name=\"id\" value=\"1\"/>")))
                .andExpect(content().string(containsString("<input type=\"text\" class=\"form-control\" id=\"name\" name=\"name\" value=\"Metropolitan Power and Light\"/>")));
        // @formatter:on
    }

    @Test
    public void loadAcctEditNew() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/acctEdit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"id\" name=\"id\" value=\"\"/>")))
                .andExpect(content().string(containsString("<input type=\"text\" class=\"form-control\" id=\"name\" name=\"name\" value=\"\"/>")));
        // @formatter:on
    }

    @Test
    public void loadAcctEditNotFound() throws Exception {
        when(acctService.findById(9L)).thenThrow(new NotFoundException(LOGGER, "Account", 9L));

        // @formatter:off
        mockMvc.perform(get("/acctEdit").param("id", "9"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("errorMessage","Account was not found for id=9."))
                .andExpect(redirectedUrl("/acctList"));
        // @formatter:on
    }

    @Test
    public void processAcctEdit() throws Exception {
        when(acctService.save(any(Acct.class))).thenReturn(acct1);

        // @formatter:off
        mockMvc.perform(post("/acctEdit")
                    .param("id", "1")
                    .param("version", "0")
                    .param("ofxOrganization", "")
                    .param("ofxFid", "")
                    .param("ofxBankId", "")
                    .param("newEntity", "false")
                    .param("oldNumber", "123456789012")
                    .param("oldEffDate", "1/1/2020")
                    .param("dataFileId", "")
                    .param("name", "Some Bill")
                    .param("type", "BILL")
                    .param("addressName", "")
                    .param("address1", "")
                    .param("address2", "")
                    .param("city", "")
                    .param("state", "")
                    .param("zipCode", "")
                    .param("phoneNumber", "")
                    .param("creditLimit", "")
                    .param("number", "123456789012")
                    .param("effDate", "1/1/2020")
                    .param("beginningBalance", ""))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("successMessage","Account successfully updated, id=1."))
                .andExpect(redirectedUrl("/acct?id=1"));
        // @formatter:on
    }

    @Test
    public void loadAcctBalance() throws Exception {
        when(acctService.findById(1L)).thenReturn(acct1);

        // @formatter:off
        mockMvc.perform(get("/acctBalance")
                    .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<p class=\"form-control-plaintext\"><a href=\"/acct?id=1\">Metropolitan Power and Light</a></p>")))
                .andExpect(content().string(containsString("<td>-¤56.78</td>")))
                .andExpect(content().string(containsString("<td>¤0.00</td>")))
                .andExpect(content().string(containsString("<td>-¤54.32</td>")))
                .andExpect(content().string(containsString("<td>¤0.00</td>")))
                .andExpect(content().string(containsString("<td>-¤41.15</td>")))
                .andExpect(content().string(containsString("<td>-¤123.45</td>")))
                .andExpect(content().string(containsString("<td>¤0.00</td>")));
        // @formatter:on
    }
}
