package jp.ats.dexter;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public abstract class RangeValidator extends AbstractValidator {

	private static final String illegalMessage = "minimum と maximum どちらかが 0 より大きい必要があります";

	private static final String nameSuffix = "範囲検査";

	private static final String rangeMessage = "{0}が {1} 以上 {2} 以下ではありません";

	private static final String maxMessage = "{0}が {1} 以下ではありません";

	private static final String minMessage = "{0}が {1} 以上ではありません";

	private static final String rangeDescription = "{0}が {1} 以上 {2} 以下であるか検査します";

	private static final String maxDescription = "{0}が {1} 以下であるか検査します";

	private static final String minDescription = "{0}が {1} 以上であるか検査します";

	private long maximum = 0;

	private long minimum = 0;

	@Override
	public void reset() {
		super.reset();
		maximum = 0;
		minimum = 0;
	}

	@Override
	public String getName() {
		return getValidationTargetName() + getNameSuffix();
	}

	@Override
	public String getMessageTemplate() {
		if (minimum > 0 && maximum > 0) return getRangeMessage();
		if (maximum > 0) return getMaxMessage();
		if (minimum > 0) return getMinMessage();
		throw new IllegalStateException(illegalMessage);
	}

	@Override
	public String getDescriptionTemplate() {
		if (minimum > 0 && maximum > 0) return getRangeDescription();
		if (maximum > 0) return getMaxDescription();
		if (minimum > 0) return getMinDescription();
		throw new IllegalStateException(illegalMessage);
	}

	@Override
	public boolean validate() {
		long target = getValidationTarget();
		if (minimum > 0 && maximum > 0)
			return minimum <= target && maximum >= target;
		if (maximum > 0) return maximum >= target;
		if (minimum > 0) return minimum <= target;
		throw new IllegalStateException(illegalMessage);
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected String getNameSuffix() {
		return nameSuffix;
	}

	protected String getRangeMessage() {
		return rangeMessage;
	}

	protected String getMaxMessage() {
		return maxMessage;
	}

	protected String getMinMessage() {
		return minMessage;
	}

	protected String getRangeDescription() {
		return rangeDescription;
	}

	protected String getMaxDescription() {
		return maxDescription;
	}

	protected String getMinDescription() {
		return minDescription;
	}

	protected abstract long getValidationTarget();

	protected abstract String getValidationTargetName();

	@Override
	protected Object[] getMessageArguments() {
		if (minimum > 0 && maximum > 0)
			return new Object[] { getValidationTargetName(), minimum, maximum };
		if (maximum > 0)
			return new Object[] { getValidationTargetName(), maximum };
		if (minimum > 0)
			return new Object[] { getValidationTargetName(), minimum };
		throw new IllegalStateException(illegalMessage);
	}

	@Override
	protected Object[] getDescriptionArguments() {
		return getMessageArguments();
	}
}
