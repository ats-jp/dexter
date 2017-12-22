package jp.ats.dexter.validator;

import java.util.regex.Pattern;

import jp.ats.dexter.AbstractRegexValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Regex;

/**
 * @author 千葉 哲嗣
 */
public class RegexValidator extends AbstractRegexValidator
	implements Validator<Regex> {

	@Override
	public void initialize(Regex validation) {
		setName(validation.name());
		setDescription(validation.description());
		setValidationMessage(validation.message());
		setPattern(Pattern.compile(validation.regex()));
	}
}
