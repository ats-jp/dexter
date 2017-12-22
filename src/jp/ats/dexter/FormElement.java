package jp.ats.dexter;

import jp.ats.dexter.annotation.Required;

/**
 * @author 千葉 哲嗣
 */
public interface FormElement {

	String getId();

	ValidatorSource[] getValidators();

	Required getRequiredAnnotation();
}
