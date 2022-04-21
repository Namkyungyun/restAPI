package com.restapi.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping(value = "api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping                //@Valid를 통해 eventDto의 필드 어노테이션을 참고해서 검증을 수행, 검증 수행 결과를 Errors에 넣어준다.
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if( errors.hasErrors() ) {
            /*응답 메시지를 클라이언트가 보기 위해서
            (errors는 body에 event를 실어 json형태로 보내지는 것과 달리 errors객체는 json으로 변환되어지지 않는다.
             그 이유는 event는 java bean 스펙을 따르기 때문에 기본으로 등록되어 있는 BeanSerialization(는 objectMapper를 씀. 따라서 json으로 변환됨)
             을 통해 json으로 변환되고, errors의 경우 java bean 스펙을 준수하지 않는다. 따라서 아무런 처리없이 body에 errors를 실을 경우 에러가 발생한다. )
            EventSerializer 클래스에서 json으로 변환하기 위해 serialization을 커스텀한 뒤, body에 errors를 싣는다.*/
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        //eventValidator 이후에도 에러가 있을 시,
        if( errors.hasErrors() ) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri(); //uri로 변환
        return ResponseEntity.created(createdUri).body(event);
    }
}
