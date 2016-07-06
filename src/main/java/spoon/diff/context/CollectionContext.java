package spoon.diff.context;

import java.util.Collection;

public class CollectionContext<T extends Collection<?>> extends Context {
	protected final T original;

	public CollectionContext(T original) {
		this.original = original;
	}
}
