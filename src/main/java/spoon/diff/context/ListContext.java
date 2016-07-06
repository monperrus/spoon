package spoon.diff.context;

import java.util.List;

public class ListContext extends CollectionContext<List<?>> {
	private final int position;

	public ListContext(List<?> original) {
		super(original);
		this.position = -1;
	}

	public ListContext(List<?> original, int position) {
		super(original);
		this.position = position;
	}
}
