package jp.ats.dexter.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.dexter.Validation;
import jp.ats.dexter.validator.DateValidator;

/**
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Validation(DateValidator.class)
public @interface Date {

	String format() default "yyyy/MM/dd";

	String name() default "日付検査";

	String description() default "日付の形式が YYYY/MM/DD となっているか検査します";

	String message() default "日付の形式が YYYY/MM/DD ではないか、正しい日付ではありません";
}
