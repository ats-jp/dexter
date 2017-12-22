package jp.ats.dexter;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 入力値の検査対象に付与するアノテーションに付与するためのアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Validation {

	Class<? extends Validator<?>> value();
}
