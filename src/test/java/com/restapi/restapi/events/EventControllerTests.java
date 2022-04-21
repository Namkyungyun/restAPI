package com.restapi.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//통합테스트
//단위 테스트라고 보기에는 dispatchServlet, eventCotroller, dataHandler, Converter들이 조합된 채로 동작되는 테스트이기에 단위기테스트라고 보기 어렵다.
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("mockMVC를 사용하기 위헤 AutoConfigureMockMvc사용")
public class EventControllerTests {

    //mock으로 만들어진 dispatchServlet (가짜 요청)
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("DTO로 입력값 제한")
    void createEvent() throws Exception{

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 04, 18, 10, 25))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,04, 19, 22,10))
                .beginEventDateTime(LocalDateTime.of(2022, 04, 20, 10, 25))
                .endEventDateTime(LocalDateTime.of(2022,04, 21, 22,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType("application/hal+json;charset=UTF-8")
                        .accept("application/hal+json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.toString()))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(false));
    }

    @Test
    @DisplayName("Bad_Requeest로 입력제한")
    void createEvent_Bad_Request() throws Exception{

        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 04, 18, 10, 25))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,04, 19, 22,10))
                .beginEventDateTime(LocalDateTime.of(2022, 04, 18, 10, 25))
                .endEventDateTime(LocalDateTime.of(2022,04, 19, 22,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType("application/hal+json;charset=UTF-8")
                        .accept("application/hal+json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("필수값 들어오지 않음")
    void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                            .contentType("application/hal+json;charset=UTF-8")
                            .content(this.objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("eventValidator를 이용해 어노테이션 미검증 커버")
    void createEvent_Bad_Request_unstable_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 05, 18, 10, 25))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,04, 19, 22,10))
                .beginEventDateTime(LocalDateTime.of(2022, 04, 18, 10, 25))
                .endEventDateTime(LocalDateTime.of(2022,03, 19, 22,10))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();
        //Bad_Request로 받을 수 있는 응답의 본문 메시지를 만드는 법
        this.mockMvc.perform(post("/api/events")
                        .contentType("application/hal+json;charset=UTF-8")
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())        //응답에 다음과 같은 응답의 데이터는 Errors에서 만들 수 있다.
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectValue").exists())
        ;

    }


}
