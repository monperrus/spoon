/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.reflect.visitor.printer.sniper;

import spoon.compiler.Environment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SniperWriter {
	private StringBuilder content = new StringBuilder();
	/**
	 * Offset of print
	 */
	private Map<Integer, Integer> offset;
	private String classContent;
	private DefaultJavaPrettyPrinter printer;
	private List<String> imports;

	SniperWriter(String content, Environment env) {
		classContent = content;
		this.content.append(content);
		offset = new HashMap<>();
		printer = new DefaultJavaPrettyPrinter(env);
		imports = getImports(content);
	}

	public SniperWriter replaceModifiers(int start, int end, CtModifiable element) {
		printer.getElementPrinterHelper().writeModifiers(element);
		String content = printer.getResult();
		printer.reset();

		return this.replace(start, end, content.trim());
	}
	public SniperWriter replace(int start, int end, String content) {
		start = getPosition(start);
		end = getPosition(end);

		this.content.delete(start, end + 1);
		this.content.insert(start,  content);
		addOffset(start, content.length() - (end + 1 - start));
		return this;
	}

	public SniperWriter write(CtElement element, int position) {
		int realPosition = getPosition(position);

		printer.getPrinter().setTabCount(getIndentation(realPosition));
		printer.getPrinter().writeTabs();
		printer.scan(element);
		String content = printer.getResult();
		printer.reset();

		ImportScanner importScanner = new ImportScannerImpl();
		Collection<CtTypeReference<?>> imports = importScanner.computeImports(element);
		List<String> missingImports = computeMissingImports(imports);
		printImports(missingImports);

		if (element instanceof CtStatement && !content.endsWith("\n") && this.content.charAt(position + 1) != '\n') {
			content += "\n";
		}

		element.setPosition(element.getFactory().createSourcePosition(null, position, position, null));
		this.content.insert(realPosition, content);
		addOffset(realPosition, content.length());
		return this;
	}

	public SniperWriter remove(CtElement element) {
		SourcePosition position = element.getPosition();
		if (position == null) {
			return this;
		}
		return remove(position.getSourceStart(), position.getSourceEnd());
	}

	public SniperWriter remove(int start, int end) {
		start = getPosition(start);
		end = getPosition(end);
		this.content.delete(start, end + 1);
		addOffset(start, start - end - 1);
		return this;
	}


	public void clear() {
		this.content = new StringBuilder();
		this.content.append(classContent);
		this.imports = getImports(classContent);
		this.offset = new HashMap<>();
	}

	private int getPosition(int position) {
		int output = position;
		List<Integer> offsets = new ArrayList<>(offset.keySet());
		Collections.sort(offsets);
		for (Integer i : offsets) {
			if (i <= output) {
				output += offset.get(i);
			}
		}
		return output;
	}

	public int getPositionNewLine(int position) {
		int originalPosition = getPosition(position) - 1;
		return content.indexOf("\n", originalPosition) - originalPosition;
	}

	private void addOffset(int start, int length) {
		if (length != 0) {
			offset.put(start, length);
		}
	}

	private void printImports(List<String> missingImports) {

	}

	private List<String> computeMissingImports(Collection<CtTypeReference<?>> neededImports) {
		List<String> imports = new ArrayList<>();
		for (Iterator<CtTypeReference<?>> iterator = neededImports.iterator(); iterator.hasNext();) {
			String neededImport = iterator.next().toString();
			//System.out.println(neededImport);
		}
		return imports;
	}

	private List<String> getImports(String sourceCode) {
		List<String> imports = new ArrayList<>();
		String[] lines = sourceCode.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.startsWith("import ")) {
				line = line.substring(7, line.length() - 1);
				if (line.startsWith("static")) {
					line = line.substring(6);
				}
				imports.add(line);
			}
		}
		return imports;
	}

	private int getIndentation(int position) {
		int output = 0;
		String[] lines = this.content.substring(position).split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.isEmpty()) {
				continue;
			}
			for (int j = 0; j < line.length(); j++) {
				if (line.charAt(j) == ' ' || line.charAt(j) == '\t') {
					output++;
				} else {
					break;
				}
			}
			break;
		}
		return output;
	}

	@Override
	public String toString() {
		return content.toString();
	}
}
