package jp.ats.dexter.validator;

import java.util.regex.Pattern;

import jp.ats.dexter.AbstractRegexValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.AlphanumericCharacter;

/**
 * @author 千葉 哲嗣
 */
public class AlphanumericCharacterValidator extends AbstractRegexValidator
	implements Validator<AlphanumericCharacter> {

	@Override
	public void initialize(AlphanumericCharacter validation) {
		setName(validation.name());
		setDescription(validation.description());
		setValidationMessage(validation.message());
		setPattern(Pattern.compile(validation.regex()));
	}
}
