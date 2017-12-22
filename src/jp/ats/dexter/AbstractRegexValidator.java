package jp.ats.dexter;

import java.util.regex.Pattern;

/**
 * @author 千葉 哲嗣
 */
public class AbstractRegexValidator extends AbstractValidator {

	private Pattern pattern;

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean validate() {
		return pattern.matcher(getValue()).matches();
	}

	@Override
	public void reset() {
		super.reset();
		pattern = null;
	}

	@Override
	protected Object[] getMessageArguments() {
		return new Object[] { pattern };
	}

	@Override
	protected Object[] getDescriptionArguments() {
		return new Object[] { pattern };
	}
}
