package com.restapi.restapi.events;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

//objectMapper에 등록하기 위해 spring-boot에서 제공하는 @JsonComponent 사용
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {
    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        //errors 클래스에서 사용하는 rejectValue는 필드에러에 해당한다. reject의 경우 global Error에 해당한다.
        //각 필드에러 마다 오브젝트를 만들기
        errors.getFieldErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("field", e.getField());
                jsonGenerator.writeStringField("code", e.getCode());
                jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
                jsonGenerator.writeStringField("objectName", e.getObjectName());
                Object rejectedValue = e.getRejectedValue();
                if(rejectedValue != null) {
                    jsonGenerator.writeStringField("rejectValue", e.getRejectedValue().toString());
                }
                jsonGenerator.writeEndObject();
                //글로벌 에러가 있다면 위와 동일하게 넣는다. 글로벌에러의 경우 필드와 rejectValue가 없다.
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        jsonGenerator.writeEndArray();
    }
}
