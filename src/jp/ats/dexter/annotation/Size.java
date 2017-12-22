package jp.ats.dexter.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.dexter.Validation;
import jp.ats.dexter.validator.LengthValidator;

/**
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Validation(LengthValidator.class)
public @interface Size {

	int size();

	String charset() default "UTF-8";

	String name() default "バイト数検査";

	String description() default "バイト数が {0} か検査します";

	String message() default "バイト数が {0} ではありません";
}
