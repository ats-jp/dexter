package jp.ats.dexter;

import jp.ats.dexter.Dexter.ValidatorResult;
import jp.ats.substrate.util.CollectionMap;

/**
 * @author 千葉 哲嗣
 */
public class DexterValidationException extends RuntimeException {

	private static final long serialVersionUID = -6792095269785943093L;

	private final CollectionMap<FormElement, ValidatorResult> result;

	DexterValidationException(
		CollectionMap<FormElement, ValidatorResult> result) {
		this.result = result.clone();
	}

	public CollectionMap<FormElement, ValidatorResult> getValidationResult() {
		return result;
	}
}
