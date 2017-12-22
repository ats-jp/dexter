package jp.ats.dexter;

import java.util.Map;

/**
 * @author 千葉 哲嗣
 */
public interface Jspod {

	JspodToken token();

	void apply(Map<String, String[]> parameterMap);
}
