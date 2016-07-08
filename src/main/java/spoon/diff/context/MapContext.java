package spoon.diff.context;

import java.util.Map;

public class MapContext extends Context {
	private final Map<?, ?> map;

	public MapContext(Map<?, ?> map) {
		this.map = map;
	}

	public Map<?, ?> getMap() {
		return map;
	}
}
