package spoon.test.template.testclasses;

import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class InvocationTemplate extends ExtensionTemplate {

	IFace iface;
	
	void invoke() {
		iface.$method$();
	}
	
	@Local
	public InvocationTemplate(CtTypeReference<?> ifaceType, String methodName) {
		this._IFace = ifaceType.getSimpleName();
		this._$method$ = methodName;
	}

	@Parameter("IFace")
	String _IFace;
	
	@Parameter("$method$")
	String _$method$;

	
	
	@Local
	interface IFace {
		void $method$();
	}
}
