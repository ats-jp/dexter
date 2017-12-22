package jp.ats.dexter;

import java.lang.annotation.Annotation;

/**
 * 入力値の検査を行うバリデータを定義するインターフェイスです。
 * <br>
 * {@link Validation} をつけたアノテーションで、このインターフェイスを実装したクラスを指定することで機能させることができます。
 *
 * @author 千葉 哲嗣
 */
public interface Validator<A extends Annotation> {

	void initialize(A validation);

	String getName();

	String getDescription();

	void setValue(String value);

	boolean validate();

	String getValidationMessage();

	void reset();
}
