package jp.ats.dexter;

/**
 * @author 千葉 哲嗣
 */
public interface Consumer<T extends Jspod> {

	/**
	 * {@link Jspod} を使用して、処理を実行します。
	 *
	 * @param jspod
	 */
	void consume(T jspod);

	/**
	 * {@link #consume(Jspod)} の結果、遷移先が変わる場合、trueを返し、{@link #getForward()} で遷移先を返します。
	 *
	 * @return
	 */
	default boolean hasForward() {
		return false;
	}

	/**
	 * {@link #hasForward()} で true を返した場合、遷移先を返す必要があります。
	 *
	 * @return
	 */
	default String getForward() {
		throw new UnsupportedOperationException();
	}
}
