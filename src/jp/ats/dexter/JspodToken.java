package jp.ats.dexter;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 千葉 哲嗣
 */
public class JspodToken implements Serializable {

	private static final long serialVersionUID = -3506211502970047470L;

	private static final SecureRandom random = new SecureRandom();

	private static AtomicLong counter = new AtomicLong();

	private final String token;

	private final String name = DexterManager.getTokenName();

	JspodToken() {
		token = generateToken();
	}

	JspodToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return token;
	}

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof JspodToken)) return false;
		return token.equals(((JspodToken) another).token);
	}

	@Override
	public int hashCode() {
		return token.hashCode();
	}

	public String name() {
		return name;
	}

	public String getQueryString() {
		return name + "=" + token;
	}

	private static String generateToken() {
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(Integer.toString((b & 0xf0) >> 4, 16));
			builder.append(Integer.toString(b & 0x0f, 16));
		}
		return builder.append("-").append(counter.getAndIncrement()).toString();
	}
}
