package jp.ats.dexter.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ats.dexter.AbstractValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Numeric;

/**
 * @author 千葉 哲嗣
 */
public class NumericValidator extends AbstractValidator
	implements Validator<Numeric> {

	private static final Pattern pattern = Pattern.compile("^([\\d,]+)$");

	private static final Pattern decimalPointPattern = Pattern
		.compile("^([\\d,]+)\\.([\\d]+)$");

	private static final Pattern integerPattern = Pattern.compile("^\\d+$");

	private static final Pattern commaPattern = Pattern
		.compile("^\\d{1,3}(,\\d{3})*$");

	private int digits;

	private int decimalPlaces;

	private boolean allowCommas;

	private boolean allowSign;

	@Override
	public void initialize(Numeric validation) {
		digits = validation.digits();
		decimalPlaces = validation.decimalPlaces();
		allowCommas = validation.allowCommas();
		allowSign = validation.allowSign();

		setName(validation.name());
		setValidationMessage(validation.message());

		String description = "整数部 "
			+ digits
			+ " 桁"
			+ (decimalPlaces > 0 ? "、少数部 " + decimalPlaces + " 桁" : "")
			+ "の数値であるか検査します";

		setDescription(description);
	}

	@Override
	public boolean validate() {
		String value = getValue();

		//符号をとってみて
		String signRemoved = value.replaceFirst("^[\\-\\+]", "");

		//符号不可なのに長さが変わっていたら不正な値
		if (!allowSign && value.length() != signRemoved.length()) {
			setValidationMessage("符号をつけることはできません");
			return false;
		}

		Matcher matcher = pattern.matcher(signRemoved);
		//少数なしパターンにマッチするのなら、小数点可でも不可でもOK
		if (matcher.matches()) return validateIntegerPart(matcher.group());

		//マッチしなければ、小数点不可の場合エラー
		if (decimalPlaces < 1) {
			setValidationMessage("少数は入力できません");
			return false;
		}

		Matcher decimalPointMatcher = decimalPointPattern.matcher(signRemoved);
		if (decimalPointMatcher.matches()) {
			if (decimalPointMatcher.group(2).length() > decimalPlaces) {
				setValidationMessage("小数点以下桁数が " + decimalPlaces + " 桁を超えています");
				return false;
			}

			return validateIntegerPart(decimalPointMatcher.group(1));
		}

		return false;
	}

	private boolean validateIntegerPart(String integer) {
		if (allowCommas) {
			//カンマ許容の場合は、カンマなしでもOK
			if (!commaPattern.matcher(integer).matches()
				&& !integerPattern.matcher(integer).matches()) return false;
			integer = integer.replaceAll(",", "");
		} else {
			if (!integerPattern.matcher(integer).matches()) return false;
		}

		if (integer.length() > digits) {
			setValidationMessage("整数部の桁数が " + digits + " 桁を超えています");
			return false;
		}

		return true;
	}

	@Override
	public void reset() {
		super.reset();
		digits = Numeric.DIGITS_DEFAULT;
		decimalPlaces = Numeric.DECIMAL_PLACES_DEFAULT;
		allowCommas = Numeric.ALLOW_COMMAS_DEFAULT;
		allowSign = Numeric.ALLOW_SIGN_DEFAULT;
	}
}
