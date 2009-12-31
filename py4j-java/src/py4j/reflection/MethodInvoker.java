package py4j.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import py4j.Py4JException;

public class MethodInvoker {

	public final static int INVALID_INVOKER_COST = -1;
	
	private int cost;

	private TypeConverter[] converters;

	private Method method;
	
	private final Logger logger = Logger.getLogger(MethodInvoker.class.getName());

	public MethodInvoker(Method method, TypeConverter[] converters, int cost) {
		super();
		this.method = method;
		this.converters = converters;
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}

	public Object invoke(Object obj, Object[] arguments) {
		Object returnObject = null;

		try {
			Object[] newArguments = arguments;

			if (converters != null) {
				int size = arguments.length;
				newArguments = new Object[size];
				for (int i = 0; i < size; i++) {
					newArguments[i] = converters[i].convert(arguments[i]);
				}
			}

			method.invoke(obj, newArguments);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Could not invoke method or received an exception while invoking.",e);
			throw new Py4JException(e);
		}

		return returnObject;
	}

	public boolean isVoid() {
		return method.getReturnType().equals(void.class);
	}
	
	public static MethodInvoker buildInvoker(Method method, Class<?>[] arguments) {
		MethodInvoker invoker = null;
		int size = arguments.length;
		int cost = 0;
		Class<?>[] parameters = method.getParameterTypes();
		List<TypeConverter> converters = new ArrayList<TypeConverter>();
		if (arguments == null || size == 0) {
			invoker = new MethodInvoker(method, null, 0);
		} else {
			for (int i = 0; i<size; i++) {
				if (parameters[i].isAssignableFrom(arguments[i])) {
					cost += computeCost(parameters[i],arguments[i]);
					converters.add(TypeConverter.NO_CONVERTER);
				} else if (TypeUtil.isNumeric(parameters[i]) && TypeUtil.isNumeric(arguments[i])) {
					
				} else if (TypeUtil.isCharacter(parameters[i])) {
					
				} else if (TypeUtil.isBoolean(parameters[i]) && TypeUtil.isBoolean(arguments[i])) {
					converters.add(TypeConverter.NO_CONVERTER);
				} else {
					cost = -1;
					break;
				}
			}
		}
		
		invoker = new MethodInvoker(method, converters.toArray(new TypeConverter[0]), cost);
		
		return invoker;
	}

	

	private static int computeCost(Class<?> parent, Class<?> child) {
		return 0;
	}

}