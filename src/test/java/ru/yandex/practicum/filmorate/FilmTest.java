package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.contoller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilmTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController controller;

    @Test
    public void test() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void getTest() throws Exception {
        this.mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void postTestNewFilm() throws Exception {
        Film film = new Film("Afterday", "dfdg",
                LocalDate.of(2012, 2, 22), 125);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    public void postTestChekName() throws Exception {
        Film film = new Film( " ", "dfdg",
                LocalDate.of(2012, 2, 22), 125);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void postTestCheckLengthDescription() throws Exception {
        Film film = new Film("Послезавтра", "Земля уверенно движется навстречу глобальной " +
                "экологической катастрофе: в одной части света все живое погибает от засухи, в другой - разбушевавшаяся" +
                " водная стихия сносит города.\n" +"Близость катастрофы вынуждает ученого-климатолога, пытающегося " +
                "найти способ остановить глобальное потепление, отправиться на поиски пропавшего сына в Нью-Йорк, в " +
                "котором наступил новый ледниковый период…",
                LocalDate.of(2004, 5, 27), 124);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void postTestCheckNamed() throws Exception {
        Film film = new Film( "Армагеддон", "Тень гигантского астероида легла на Землю.",
                LocalDate.of(1798, 6, 30), 144);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void putTestCheckDuration() throws Exception {
        Film film = new Film( "Армагеддон", "Тень гигантского астероида легла на Землю.",
                LocalDate.of(1998, 6, 30), -144);
        this.mockMvc.perform(post("/films")
                        .content(asJsonString(film))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule((new JavaTimeModule()));
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
