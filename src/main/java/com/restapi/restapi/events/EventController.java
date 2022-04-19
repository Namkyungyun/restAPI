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
            return ResponseEntity.badRequest().build();
        }

        eventValidator.validate(eventDto, errors);
        //eventValidator 이후에도 에러가 있을 시,
        if( errors.hasErrors() ) {
            return ResponseEntity.badRequest().build();
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri(); //uri로 변환
        return ResponseEntity.created(createdUri).body(event);
    }
}
