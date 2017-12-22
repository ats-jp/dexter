package jp.ats.dexter.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.dexter.Validation;
import jp.ats.dexter.validator.RegexValidator;

/**
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Validation(RegexValidator.class)
public @interface Regex {

	int size();

	String regex();

	String name() default "正規表現検査";

	String description() default "正規表現 {0} と一致するか検査します";

	String message() default "正規表現 {0} と一致しません";
}
