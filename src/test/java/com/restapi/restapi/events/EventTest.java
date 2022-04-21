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

    @Test
    void testFree() {
        /*무료인 경우*/
        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();
        
        /*무료가 아닌 경우, baseprice 존재*/
        //given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
        
        /* 무료가 아닌 경우, maxPrice가 존재*/
        //given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    
    }

    @Test
    void testOffline() {
        /* location이 있을 때 */
        //given
        Event event = Event.builder()
                .location("location_true")
                .build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isTrue();
        
        /* location이 빈값일때 */
        //given
        event = Event.builder().build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isFalse();
    }
}