package jp.ats.dexter.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.dexter.Validation;
import jp.ats.dexter.validator.AlphanumericCharacterValidator;

/**
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Validation(AlphanumericCharacterValidator.class)
public @interface AlphanumericCharacter {

	String regex() default "^\\p{Alnum}*$";

	String name() default "半角英数検査";

	String description() default "すべての文字が半角英数であるか検査します";

	String message() default "半角英数ではない文字が含まれています";
}
