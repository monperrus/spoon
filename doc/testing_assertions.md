---
title: Assertions
tags: [assert, assertions]
keywords: testing, assert, assertion
last_updated: February 10, 2015
---

## Types

For now, you have two type of assertion from the entry point, `Assert` class:

Assert type | Description
-------------|------------
FileAssert | Assertions available on a file.
CtElementAssert | Assertions available on a `CtElement`.

If you have any idea for an assert type, pull request is welcome on the Github Spoon project!

## Testing transformation

This module has been developed to test your transformations. This feature is available for
all types of assertions and works with processors (templates will come soon).

Let's say that you have a processor which change the name of all fields by the name "j".

```java
class MyProcessor extends AbstractProcessor<CtField<?>> {
	@Override
	public void process(CtField<?> element) {
		element.setSimpleName("j");
	}
}
```

You want to check that the transformation is well done when you apply it on an actual class, 
you should call the method `withProcessor` with an instance, a class access or its fully 
qualified name.

A simply example can be the code below where the processor is specified by an instance. 

```java
final SpoonAPI spoon = new Launcher();
spoon.addInputResource("path/of/my/file/Foo.java");
spoon.run();

final CtType<Foo> type = spoon.getFactory().Type().get(Foo.class);
assertThat(type.getField("i")).withProcessor(new MyProcessor()).isEqualTo("public int j;");
```

We let you explore all possibilities of our API of this testing module.