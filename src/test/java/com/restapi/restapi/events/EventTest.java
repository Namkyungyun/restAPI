package com.restapi.restapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

//도메인 테스트
class EventTest {

    @Test
    void builider() {
        Event event = Event.builder()
                .name("test spring rest api")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    void javaBean() {
        //given
        String name = "event";
        String description = "event Setter, All_argument, No_argument";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}