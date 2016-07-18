package spoon.reflect.visitor;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

public class Writer {
	private StringBuilder content = new StringBuilder();
	private int[] offset;
	private String classContent;

	Writer(String content) {
		classContent = content;
		this.content.append(content);
		offset = new int[content.split("\n").length];
	}

	public Writer replace(int start, int end, String content) {
		this.content.delete(start, end + 1);
		this.content.insert(start,  content);
		return this;
	}

	public Writer write(CtElement element, int position) {
		this.content.insert(position, element.toString());
		return this;
	}

	public Writer remove(CtElement element) {
		SourcePosition position = element.getPosition();
		if (position == null) {
			return this;
		}
		int start = position.getSourceStart();
		int end = position.getSourceStart();
		this.content.delete(start, end + 1);
		return this;
	}


	public void clear() {
		this.content = new StringBuilder();
		this.content.append(classContent);
	}


	@Override
	public String toString() {
		return content.toString();
	}
}