package com.aegarland.restexample.provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
/** this class is not needed (maven dependency on jackson-jaxrs-json-provider)

 see http://blog.dejavu.sk/2014/02/11/inject-custom-java-types-via-jax-rs-parameter-annotations/
 for discussion of Json -> POJO for parameters (this comes directly from there)
 */
public class JacksonJsonParamConverterProvider implements ParamConverterProvider {

    @Context
    private Providers providers;

    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType,
                                              final Type genericType,
                                              final Annotation[] annotations) {
        // Check whether we can convert the given type with Jackson.
        final MessageBodyReader<T> mbr = providers.getMessageBodyReader(rawType,
                genericType, annotations, MediaType.APPLICATION_JSON_TYPE);
        if (mbr == null
              || !mbr.isReadable(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE)) {
            return null;
        }

        // Obtain custom ObjectMapper for special handling.
        final ContextResolver<ObjectMapper> contextResolver = providers
                .getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);

        final ObjectMapper mapper = contextResolver != null ?
                contextResolver.getContext(rawType) : new ObjectMapper();
                
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,true);
        (new Exception("custom converter")).printStackTrace();

        // Create ParamConverter.
        return new ParamConverter<T>() {

            @Override
            public T fromString(final String value) {
                try {
                    return mapper.reader(rawType).readValue(value);
                } catch (IOException e) {
                    throw new ProcessingException(e);
                }
            }

            @Override
            public String toString(final T value) {
                try {
                    return mapper.writer().writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new ProcessingException(e);
                }
            }
        };
    }
}