/*
 * Copyright 2002-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.flex.core.io;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
//import org.springframework.core.convert.support.PropertyTypeDescriptor;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

/**
 * {@link GenericConverter} implementation that converts from {@link HibernateProxy} to {@code Object} and will either:
 * <ul>
 * <li>Convert to null if the {@code HibernateProxy} instance is uninitialized</li>
 * <li>Convert to the underlying proxied class if the {@code HibernateProxy} is initialized</li>
 * </ul>
 *
 * @author Jeremy Grelle
 */
public class HibernateProxyConverter implements GenericConverter {
//TODO: Revisar, pues ahora se procesan todos, y debe ser posible discriminar cuando no hacerlo.
	/**
	 * {@inheritDoc}
	 */
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Assert.isInstanceOf(HibernateProxy.class, source, "Expected an instance of HibernateProxy to convert");
		Assert.isAssignable(HibernateProxy.class, sourceType.getType(), "Expected a subclass of HibernateProxy for the source type");
		HibernateProxy proxy = HibernateProxy.class.cast(source);
		/*if (targetType instanceof PropertyTypeDescriptor || targetType.getField() != null) {
			if (!Hibernate.isInitialized(proxy)) {
				return null;
			}
		}*/
		return proxy.getHibernateLazyInitializer().getImplementation();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(HibernateProxy.class, Object.class));
	}

}
