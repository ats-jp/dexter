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
public @interface Length {

	int length();

	String name() default "文字数検査";

	String description() default "文字数が {0} 文字か検査します";

	String message() default "文字数が {0} ではありません";
}
