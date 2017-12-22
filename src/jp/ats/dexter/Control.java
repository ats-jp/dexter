package jp.ats.dexter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * {@link DispatcherServlet} にフォーム入力値を送信後の振る舞いを定義するアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Control {

	/**
	 * チェックを通った値を持った {@link Jspod} を使用する処理クラスを記述します。
	 *
	 * @return {@link Consumer} 実装クラス
	 */
	Class<? extends Consumer<? extends Jspod>> consumer() default BlankConsumer.class;

	/**
	 * このアノテーションを付ける {@link Jspod} のデフォルトの遷移先を記述します。
	 * <br>
	 * {@link Consumer} が遷移先を持つ場合、そちらが使用されます。
	 *
	 * @return 遷移先 URI
	 */
	String forward();

	/**
	 * Location ヘッダを使用して遷移させたい場合、true とします。
	 *
	 * @return Location ヘッダを使用する / しない
	 */
	boolean redirect() default false;
}
