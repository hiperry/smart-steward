package com.ejushang.steward.common.jackson;

import com.ejushang.steward.common.domain.util.ViewEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * User: liubin
 * Date: 14-5-21
 */
public class EnumSerializer extends JsonSerializer<ViewEnum> {

    @Override
    public void serialize(ViewEnum viewEnum, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(viewEnum.getName());
        generator.writeFieldName("value");
        generator.writeString(viewEnum.getValue());
        generator.writeEndObject();
    }
}
