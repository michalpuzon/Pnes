package org.ekipa.pnes.api.configs.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
public @interface SecuredMapping {
    String[] role() default "";

    @AliasFor(annotation = RequestMapping.class)
    RequestMethod[] method() default RequestMethod.GET;

    @AliasFor(annotation = RequestMapping.class)
    String path() default "";
}
