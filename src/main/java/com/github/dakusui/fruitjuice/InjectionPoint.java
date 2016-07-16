package com.github.dakusui.fruitjuice;

import com.github.dakusui.fixture.Fixture;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractList;

import static com.google.common.base.Preconditions.checkNotNull;

public interface InjectionPoint {
	Provider<?> getProvider();

	InjectionRequest getRequest();

	Object getOwner();

	abstract class Base implements InjectionPoint {
		@Override
		public Provider<?> getProvider() {
			try {
				//noinspection ConstantConditions
				return getAnnotatedAnnotation(RequestInjection.class).provider().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw Throwables.propagate(e);
			}
		}

		private <T extends Annotation> T getAnnotatedAnnotation(Class<T> annotation) {
			for (Annotation each : this.getRequest().getAnnotations()) {
				if (each.annotationType().isAnnotationPresent(annotation)) {
					return each.annotationType().getAnnotation(annotation);
				};
			}
			return null;
		}
	}

	enum Factory {
		;

		static InjectionPoint createFromField(final Field targetField) {
			return new InjectionPoint.Base() {
				@Override
				public InjectionRequest getRequest() {
					return InjectionRequest.Factory.createFromField(targetField);
				}

				@Override
				public Object getOwner() {
					return targetField;
				}
			};
		}

		public static Iterable<InjectionPoint> createInjectionPointsFromConstructor(final Constructor targetConstructor) {
			return new AbstractList<InjectionPoint>() {
				@Override
				public InjectionPoint get(final int index) {
					return new InjectionPoint.Base() {
						@Override
						public InjectionRequest getRequest() {
							return new InjectionRequest.Base(targetConstructor.getParameterAnnotations()[index]) {
								@Override
								public Class<?> getType() {
									return targetConstructor.getParameterTypes()[index];
								}

								@Override
								public String getName() {
									return String.format("p%d", index);
								}
							};
						}

						@Override
						public Object getOwner() {
							return targetConstructor;
						}
					};
				}

				@Override
				public int size() {
					return targetConstructor.getParameterTypes().length;
				}
			};
		}
	}
}
