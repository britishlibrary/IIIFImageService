package uk.bl.iiifimageservice.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * When no format is specified in the request (still valid according to the spec) the Spring MVC content negotiation
 * gets a little trickier. This stackoverflow answer helped resolve the problem - <a
 * href="http://stackoverflow.com/questions/3616359/who-sets-response-content-type-in-spring-mvc-responsebody">Who sets
 * response content-type in Spring MVC (@ResponseBody)?</a>
 * 
 * @author pblake
 * 
 */
public class ByteArrayPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            List<HttpMessageConverter<?>> convs = ((RequestMappingHandlerAdapter) bean).getMessageConverters();
            for (HttpMessageConverter<?> conv : convs) {
                if (conv instanceof ByteArrayHttpMessageConverter) {
                    ((ByteArrayHttpMessageConverter) conv).setSupportedMediaTypes(Arrays.asList(new MediaType("image",
                            "jpeg"), new MediaType("image", "png"), new MediaType("image", "gif"), new MediaType(
                            "application", "octet-stream")));
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        return bean;
    }
}