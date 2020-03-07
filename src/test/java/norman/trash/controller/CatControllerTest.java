package norman.trash.controller;

import norman.trash.FakeDataUtil;
import norman.trash.domain.Cat;
import norman.trash.exception.NotFoundException;
import norman.trash.service.CatService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CatController.class)
public class CatControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatControllerTest.class);
    private Cat cat1;
    private Cat cat2;
    private Cat cat3;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CatService catService;

    @Before
    public void setUp() throws Exception {
        cat1 = FakeDataUtil.buildCat(1L, "Utilities");
        cat2 = FakeDataUtil.buildCat(2L, "Groceries");
        cat3 = FakeDataUtil.buildCat(3L, "Mortgage");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadCatList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "name", "id");
        List<Cat> content = Arrays.asList(cat2, cat3, cat1);
        Page<Cat> page = new PageImpl<>(content, pageable, 3L);
        when(catService.findAll(any(Pageable.class))).thenReturn(page);

        // @formatter:off
        mockMvc.perform(get("/catList"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<td>Groceries</td>")))
                .andExpect(content().string(containsString("<td>Mortgage</td>")))
                .andExpect(content().string(containsString("<td>Utilities</td>")));
        // @formatter:on
    }

    @Test
    public void loadCatView() throws Exception {
        when(catService.findById(1L)).thenReturn(cat1);

        // @formatter:off
        mockMvc.perform(get("/cat").param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<p class=\"form-control-plaintext\">Utilities</p>")));
        // @formatter:on
    }

    @Test
    public void loadCatViewNotFound() throws Exception {
        when(catService.findById(9L)).thenThrow(new NotFoundException(LOGGER, "Category", 9L));

        // @formatter:off
        mockMvc.perform(get("/cat").param("id", "9"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("errorMessage","Category was not found for id=9."))
                .andExpect(redirectedUrl("/catList"));
        // @formatter:on
    }

    @Test
    public void loadCatEdit() throws Exception {
        when(catService.findById(1L)).thenReturn(cat1);

        // @formatter:off
        mockMvc.perform(get("/catEdit").param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"id\" name=\"id\" value=\"1\"/>")))
                .andExpect(content().string(containsString("<input type=\"text\" class=\"form-control\" id=\"name\" name=\"name\" value=\"Utilities\"/>")));
        // @formatter:on
    }

    @Test
    public void loadCatEditNew() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/catEdit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<input type=\"hidden\" id=\"id\" name=\"id\" value=\"\"/>")))
                .andExpect(content().string(containsString("<input type=\"text\" class=\"form-control\" id=\"name\" name=\"name\" value=\"\"/>")));
        // @formatter:on
    }

    @Test
    public void loadCatEditNotFound() throws Exception {
        when(catService.findById(9L)).thenThrow(new NotFoundException(LOGGER, "Category", 9L));

        // @formatter:off
        mockMvc.perform(get("/catEdit").param("id", "9"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("errorMessage","Category was not found for id=9."))
                .andExpect(redirectedUrl("/catList"));
        // @formatter:on
    }

    @Test
    public void processCatEdit() throws Exception {
        when(catService.save(any(Cat.class))).thenReturn(cat1);

        // @formatter:off
        mockMvc.perform(post("/catEdit")
                    .param("id", "1")
                    .param("version", "0")
                    .param("name", "A new name."))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(flash().attribute("successMessage","Category successfully updated, id=1."))
                .andExpect(redirectedUrl("/cat?id=1"));
        // @formatter:on
    }
}
