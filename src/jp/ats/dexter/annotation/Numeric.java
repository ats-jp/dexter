package jp.ats.dexter.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jp.ats.dexter.Validation;
import jp.ats.dexter.validator.NumericValidator;

/**
 * @author 千葉 哲嗣
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Validation(NumericValidator.class)
public @interface Numeric {

	static final int DIGITS_DEFAULT = 19;

	static final int DECIMAL_PLACES_DEFAULT = 0;

	static final boolean ALLOW_COMMAS_DEFAULT = true;

	static final boolean ALLOW_SIGN_DEFAULT = true;

	/**
	 * 桁数
	 */
	int digits() default DIGITS_DEFAULT;

	/**
	 * 小数点以下桁数
	 */
	int decimalPlaces() default DECIMAL_PLACES_DEFAULT;

	/**
	 * カンマ区切りを許容するか
	 */
	boolean allowCommas() default ALLOW_COMMAS_DEFAULT;

	/**
	 * 正負符号を許容するか
	 */
	boolean allowSign() default ALLOW_SIGN_DEFAULT;

	String name() default "数値検査";

	String message() default "不正な数値です";
}
