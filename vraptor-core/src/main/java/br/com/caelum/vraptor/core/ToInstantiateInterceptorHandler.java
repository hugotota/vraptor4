/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.core;

import javax.enterprise.inject.Vetoed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;

/**
 * Instantiates the interceptor on the fly and executes its method.
 *
 * @author Guilherme Silveira
 */
@Vetoed
public class ToInstantiateInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ToInstantiateInterceptorHandler.class);

	private final Container container;
	private final Class<?> type;

	public ToInstantiateInterceptorHandler(Container container, Class<?> type) {
		this.container = container;
		this.type = type;
	}

	@Override
	public void execute(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
			throws InterceptionException {
		Interceptor interceptor = (Interceptor) container.instanceFor(type);
		if (interceptor == null) {
			throw new InterceptionException("Unable to instantiate interceptor for " + type.getName()
					+ ": the container returned null.");
		}
		if (interceptor.accepts(method)) {
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			interceptor.intercept(stack, method, controllerInstance);
		} else {
			stack.next(method, controllerInstance);
		}
	}

	@Override
	public String toString() {
		return "ToInstantiateHandler for " + type.getName();
	}
}
