package jp.ats.dexter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 千葉 哲嗣
 */
public class TokenJspod implements Jspod {

	private final JspodToken token;

	public TokenJspod() {
		token = new JspodToken();
	}

	public TokenJspod(HttpServletRequest request) {
		token = DexterManager.getToken();
	}

	@Override
	public JspodToken token() {
		return token;
	}

	@Override
	public void apply(Map<String, String[]> parameterMap) {}

	public String tokenName() {
		return DexterManager.getTokenName();
	}

	public String tokenValue() {
		return token.toString();
	}
}
