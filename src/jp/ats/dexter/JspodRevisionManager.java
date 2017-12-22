package jp.ats.dexter;

import java.util.Map;

import jp.ats.substrate.revision.OldRevisionException;
import jp.ats.substrate.revision.Precondition;
import jp.ats.substrate.revision.Revision;
import jp.ats.substrate.revision.RevisionManager;
import jp.ats.substrate.revision.RevisionRepository;

/**
 * @author 千葉 哲嗣
 */
public class JspodRevisionManager<T> {

	private static final String key = JspodRevisionManager.class.getName()
		+ ".key";

	private final RevisionManager<T> manager;

	public JspodRevisionManager(RevisionRepository<T> repository) {
		manager = new RevisionManager<T>(repository);
	}

	public void start(T target) {
		Revision revision = manager.get(target);
		Map<String, Container<T>> map = DexterManager.getTokenMap();
		synchronized (map) {
			map.put(key, new Container<T>(target, revision));
		}
	}

	public void finish(Precondition precondition) throws OldRevisionException {
		Map<String, Container<T>> map = DexterManager.getTokenMap();
		Container<T> container;
		synchronized (map) {
			container = map.get(key);
			if (container == null)
				throw new IllegalStateException("start が実行されていません");
		}

		manager.next(container.target, container.revision, precondition);
	}

	public T getCurrentTarget() {
		Map<String, Container<T>> map = DexterManager.getTokenMap();
		synchronized (map) {
			Container<T> container = map.get(key);
			if (container == null) return null;
			return container.target;
		}

	}

	private static class Container<T> {

		private final T target;

		private final Revision revision;

		private Container(T target, Revision revision) {
			this.target = target;
			this.revision = revision;
		}
	}
}
