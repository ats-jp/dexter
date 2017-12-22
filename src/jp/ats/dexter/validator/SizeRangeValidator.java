package jp.ats.dexter.validator;

import java.io.UnsupportedEncodingException;

import jp.ats.dexter.RangeValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.SizeRange;
import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class SizeRangeValidator extends RangeValidator
	implements Validator<SizeRange> {

	private String name;

	private String charset;

	@Override
	public void initialize(SizeRange validation) {
		name = validation.name();
		charset = validation.charset();
		setMaximum(validation.max());
		setMinimum(validation.min());
	}

	@Override
	public void reset() {
		super.reset();
		name = null;
		charset = null;
	}

	@Override
	protected long getValidationTarget() {
		if (charset == null || charset.length() == 0)
			return getValue().getBytes().length;
		try {
			return getValue().getBytes(charset).length;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(charset + " は不正な文字セットです", e);
		}
	}

	@Override
	protected String getValidationTargetName() {
		return name;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
