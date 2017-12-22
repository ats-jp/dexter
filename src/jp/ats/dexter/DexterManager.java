package jp.ats.dexter;

import static jp.ats.substrate.U.isAvailable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class DexterManager implements Filter {

	private static final String containersAttributeName = DexterManager.class
		.getName() + ".containers";

	private static final String defaultTokenName = "token";

	private static final ThreadLocal<JspodToken> newTokenThreadLocal = new ThreadLocal<JspodToken>();

	private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();

	private static String tokenName = defaultTokenName;

	private static int expireMinutes;

	private static boolean savePostdata;

	private String encoding;

	public static <K, V> Map<K, V> getTokenMap() {
		Container<K, V> container = getContainer();
		synchronized (container) {
			Map<K, V> map = container.map;
			if (map == null) {
				map = Collections.synchronizedMap(new HashMap<K, V>());
				container.map = map;
			}

			return map;
		}
	}

	public static Jspod newJspod(Class<? extends Jspod> jspodClass) {
		Jspod jspod;
		try {
			jspod = jspodClass.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		getOrCreateContainer(jspod.token());

		return jspod;
	}

	public static Jspod newBlankJspod() {
		return new TokenJspod();
	}

	public static String getTokenName() {
		synchronized (DexterManager.class) {
			return tokenName;
		}
	}

	public static JspodToken getToken() {
		String tokenString;

		HttpServletRequest request = requestThreadLocal.get();

		if (request == null
			|| !isAvailable((tokenString = request.getParameter(tokenName))))
			throw new TokenNotFoundException();

		JspodToken token = new JspodToken(tokenString);

		Map<JspodToken, Container<Object, Object>> containers = getContainers();
		synchronized (containers) {
			Container<Object, Object> container = containers.get(token);

			if (container == null) throw new TokenNotFoundException();

			if (container.expired(System.currentTimeMillis())) {
				containers.remove(token);
				throw new ExpiredTokenException();
			}
		}

		return token;
	}

	public static JspodToken generateToken() {
		JspodToken token = newTokenThreadLocal.get();
		if (token != null) return token;

		token = new JspodToken();

		newTokenThreadLocal.set(token);

		return token;
	}

	public static JspodToken generateToken(Map<String, String[]> parameters) {
		String[] values = parameters.get(getTokenName());

		if (values != null && values.length > 0) {
			String tokenString = values[0];
			if (U.isAvailable(tokenString)) return new JspodToken(tokenString);
		}

		return generateToken();
	}

	public static boolean isValidToken() {
		try {
			getToken();
			return true;
		} catch (TokenNotFoundException e) {} catch (ExpiredTokenException e) {}

		return false;
	}

	public static boolean removeToken() {
		try {
			Map<JspodToken, ?> containers = getContainers();
			synchronized (containers) {
				containers.remove(getToken());
				return true;
			}
		} catch (TokenNotFoundException e) {} catch (ExpiredTokenException e) {}

		return false;
	}

	public static void removeExpiredToken() {
		Map<JspodToken, Container<Object, Object>> containers = getContainers();
		long now = System.currentTimeMillis();
		synchronized (containers) {
			for (Iterator<Entry<JspodToken, Container<Object, Object>>> iterator = containers
				.entrySet().iterator(); iterator.hasNext();) {
				if (iterator.next().getValue().expired(now)) iterator.remove();
			}
		}
	}

	public static void removeAll() {
		Map<JspodToken, Container<Object, Object>> containers = getContainers();
		synchronized (containers) {
			containers.clear();
		}
	}

	public static synchronized int getExpireMinutes() {
		return expireMinutes;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String myTokenName = config.getInitParameter("jspod-token-name");
		if (isAvailable(myTokenName)) {
			synchronized (DexterManager.class) {
				tokenName = myTokenName;
			}
		}

		String expireMinutes = config.getInitParameter("expire-minutes");
		if (isAvailable(expireMinutes)) {
			synchronized (DexterManager.class) {
				DexterManager.expireMinutes = Integer.parseInt(expireMinutes);
			}
		}

		String savePostdata = config.getInitParameter("save-postdata");
		if (isAvailable(savePostdata)) {
			synchronized (DexterManager.class) {
				DexterManager.savePostdata = Boolean.valueOf(savePostdata);
			}
		}

		encoding = Utilities.readEncodingConfig(config.getServletContext());
	}

	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(encoding);

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		try {
			requestThreadLocal.set(httpRequest);

			//Jersey対策：後の処理のために、先にrequestのparameterを読み込まないように、tokenは必要になったときにparameterから取り込むようにする

			//Jersey対策：Jersey等、InputStreamを読み込んでしまうフレームワーク向けにInputStreamを退避する
			if (savePostdata) request = new InputStreamRefuger(httpRequest);

			chain.doFilter(request, response);
		} finally {
			requestThreadLocal.set(null);

			//tokenが生成された場合、セットされているので、クリア
			newTokenThreadLocal.set(null);
		}
	}

	/**
	 * InputStreamをすり替える
	 */
	private static class InputStreamRefuger extends HttpServletRequestWrapper {

		private final InputStream dummy;

		public InputStreamRefuger(HttpServletRequest request) {
			super(request);
			dummy = new ByteArrayInputStream("0=0".getBytes());
		}

		@Override
		public ServletInputStream getInputStream() {
			return new ServletInputStream() {

				@Override
				public int read() throws IOException {
					return dummy.read();
				}
			};
		}
	}

	@Override
	public void destroy() {}

	@Override
	public String toString() {
		return U.toString(this);
	}

	static HttpServletRequest getRequest() {
		return requestThreadLocal.get();
	}

	private static <K, V> Map<JspodToken, Container<K, V>> getContainers() {
		HttpSession session = requestThreadLocal.get().getSession();
		Map<JspodToken, Container<K, V>> containers;
		synchronized (DexterManager.class) {
			containers = castToContainerMap(
				session.getAttribute(containersAttributeName));
			if (containers == null) {
				containers = new HashMap<>();
				session.setAttribute(containersAttributeName, containers);
			}
		}

		return containers;
	}

	private static <K, V> Container<K, V> getContainer() {
		return getOrCreateContainer(getToken());
	}

	private static <K, V> Container<K, V> getOrCreateContainer(
		JspodToken token) {
		Map<JspodToken, Container<K, V>> containers = getContainers();
		synchronized (containers) {
			Container<K, V> container = containers.get(token);
			if (container == null) {
				container = new Container<K, V>();
				containers.put(token, container);
			}

			return container;
		}
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Map<JspodToken, Container<K, V>> castToContainerMap(
		Object target) {
		return (Map<JspodToken, Container<K, V>>) target;
	}

	private static class Container<K, V> {

		private final boolean validExpiredMinutes;

		private final long expire;

		private Map<K, V> map;

		private Container() {
			int expiredMinutes = getExpireMinutes();
			validExpiredMinutes = expiredMinutes != 0;
			expire = System.currentTimeMillis() + expiredMinutes * 60 * 1000;
		}

		private boolean expired(long now) {
			return validExpiredMinutes && expire < now;
		}
	}
}
