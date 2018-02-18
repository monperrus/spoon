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
package spoon.support;

import org.apache.log4j.Level;
import spoon.SpoonException;
import spoon.processing.ProcessInterruption;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.support.util.Timer;
import spoon.support.visitor.ProcessingVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This processing manager applies the processors one by one from the given root element.
 * for p : processors
 *   p.process(el)
 * Default processor in Spoon
 */
public class QueueProcessingManager implements ProcessingManager {
	Processor<?> current;

	Factory factory;

	List<Processor<?>> processors;

	ProcessingVisitor visitor;

	/**
	 * Creates a new processing manager that maintains a queue of processors to
	 * be applied to a given factory.
	 *
	 * @param factory
	 * 		the factory on which the processing applies (contains the
	 * 		meta-model)
	 */
	public QueueProcessingManager(Factory factory) {
		super();
		setFactory(factory);
	}

	public void addProcessor(Class<? extends Processor<?>> type) {
		try {
			Processor<?> p = type.newInstance();
			p.setFactory(factory);
			p.init();
			addProcessor(p);
		} catch (Exception e) {
			factory.getEnvironment().report(null, Level.ERROR, "Unable to instantiate processor \"" + type.getName() + "\" - Your processor should have a constructor with no arguments");
		}
	}

	public boolean addProcessor(Processor<?> p) {
		p.setFactory(getFactory());
		return getProcessors().add(p);
	}

	@SuppressWarnings("unchecked")
	public void addProcessor(String qualifiedName) {
		try {
			addProcessor((Class<? extends Processor<?>>) getFactory().getEnvironment().getInputClassLoader().loadClass(qualifiedName));
		} catch (ClassNotFoundException e) {
			factory.getEnvironment().report(null, Level.ERROR, "Unable to load processor \"" + qualifiedName + "\" - Check your classpath.");
		}
	}

	public Processor<?> getCurrentProcessor() {
		return current;
	}

	public Factory getFactory() {
		return factory;
	}

	public List<Processor<?>> getProcessors() {
		if (processors == null) {
			processors = new LinkedList<>();
		}
		return processors;
	}

	private ProcessingVisitor getVisitor() {
		if (visitor == null) {
			visitor = new ProcessingVisitor(getFactory());
		}
		return visitor;
	}

	public void process(Collection<? extends CtElement> elements) {
		for (Processor<?> p : getProcessors()) {
			current = p;
			process(elements, p);
		}
	}

	/**
	 * Recursively processes elements and their children with a given processor.
	 */
	public void process(Collection<? extends CtElement> elements, Processor<?> processor) {
		try {
			getFactory().getEnvironment().debugMessage("processing with '" + processor.getClass().getName() + "'...");
			current = processor;
			Timer.start(processor.getClass().getName());
			for (CtElement e : elements) {
				getFactory().getEnvironment().debugMessage(
						"processing '" + ((e instanceof CtNamedElement) ? ((CtNamedElement) e).getSimpleName() : e.toString()) + "' with '" + processor.getClass().getName() + "'...");
				processor.init();
				getVisitor().setProcessor(processor);
				getVisitor().scan(e);
				processor.processingDone();
			}
			Timer.stop(processor.getClass().getName());
		} catch (ProcessInterruption ignored) {
		}
	}

	public void process(CtElement element) {
		for (Processor<?> p : getProcessors()) {
			current = p;
			getFactory().getEnvironment().debugMessage(
					"processing '" + ((element instanceof CtNamedElement) ? ((CtNamedElement) element).getSimpleName() : element.toString()) + "' with '" + p.getClass().getName() + "'...");
			p.init();
			getVisitor().setProcessor(p);
			getVisitor().scan(element);
			p.processingDone();
		}
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
		factory.getEnvironment().setManager(this);
	}


}
