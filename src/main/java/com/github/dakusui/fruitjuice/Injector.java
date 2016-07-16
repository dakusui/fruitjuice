package com.github.dakusui.fruitjuice;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public interface Injector {
	<T> T getInstance(Class<T> targetClass);

	class Impl implements Injector {
		private final Context context;

		Impl(Context context) {
			this.context = Preconditions.checkNotNull(context);
		}

		@Override
		public <T> T getInstance(Class<T> targetClass) {
			Preconditions.checkNotNull(targetClass);
			T ret = createObject(this.context, targetClass);
			injectFields(this.context, ret, getFieldInjectionPoints(targetClass));
			return ret;
		}

		private <T> T createObject(final Context context, Class<T> targetClass) {
			try {
				final Constructor<T> constructor = getAppropriateConstructorFrom(targetClass);
				return constructor.newInstance(toArray(
						transform(
								filter(getConstructorInjectionPoints(constructor),
										new Predicate<InjectionPoint>() {
											@Override
											public boolean apply(InjectionPoint injectionPoint) {
												return constructor.equals(injectionPoint.getOwner());
											}
										}
								),
								new Function<InjectionPoint, Object>() {
									@Override
									public Object apply(InjectionPoint injectionPoint) {
										return getInstanceFor(injectionPoint, context);
									}
								}
						),
						Object.class
						)
				);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw Throwables.propagate(e);
			}
		}

		private <T> Constructor<T> getAppropriateConstructorFrom(Class<T> targetClass) {
			Preconditions.checkNotNull(targetClass);
			//noinspection unchecked
			Iterable<Constructor<T>> constructors = filter(
					asList((Constructor<T>[]) targetClass.getConstructors()),
					new Predicate<Constructor<T>>() {
						@Override
						public boolean apply(Constructor<T> constructor) {
							return constructor.isAnnotationPresent(UseForInjection.class);
						}
					}
			);
			if (size(constructors) == 1) {
				return makeSureAllParametersRequestInjection(Iterables.get(constructors, 0));
			}
			if (size(constructors) == 0) {
				try {
					return targetClass.getConstructor();
				} catch (NoSuchMethodException e) {
					throw Throwables.propagate(e);
				}
			}
			throw new RuntimeException(format(
					"More than one constructors annotated with '%s' are found in '%s'",
					UseForInjection.class.getSimpleName(),
					targetClass.getCanonicalName()
			));
		}

		private <T> Constructor<T> makeSureAllParametersRequestInjection(Constructor<T> tConstructor) {
			checkArgument(isEmpty(filter(
					asList(tConstructor.getParameterAnnotations()),
					Predicates.not(Utils.HAS_ONLY_ONE_REQUEST_INJECTION))),
					"Constructor '%s/%s' has some invalid parameter(s) which is not annotated with '%s' or annotated with it more than once.",
					tConstructor.getDeclaringClass().getCanonicalName(),
					tConstructor.getParameterAnnotations().length,
					RequestInjection.class.getCanonicalName()
			);
			return tConstructor;
		}

		private <T> void injectFields(Context context, T target, Iterable<InjectionPoint> injectionPoints) {
			for (InjectionPoint eachInjectionPoint : injectionPoints) {
				Object owner = eachInjectionPoint.getOwner();
				if (owner instanceof Field) {
					Field f = (Field) owner;
					if (f.getDeclaringClass().isInstance(target)) {
						try {
							f.set(target, getInstanceFor(eachInjectionPoint, context));
						} catch (IllegalAccessException e) {
							throw Throwables.propagate(e);
						}
					}
				}
			}
		}

		private Object getInstanceFor(InjectionPoint injectionPoint, Context context) {
			return injectionPoint.getProvider().getInstanceFor(injectionPoint.getRequest(), context);
		}

		private <T> Iterable<InjectionPoint> getFieldInjectionPoints(Class<T> targetClass) {
			return Iterables.transform(
					Utils.getTargetFieldsFromClass(targetClass),
					new Function<Field, InjectionPoint>() {
						@Override
						public InjectionPoint apply(Field each) {
							return InjectionPoint.Factory.createFromField(each);
						}
					}
			);
		}

		private <T> Iterable<InjectionPoint> getConstructorInjectionPoints(final Constructor<T> targetConstructor) {
			return InjectionPoint.Factory.createInjectionPointsFromConstructor(targetConstructor);
		}

	}

	enum Utils {
		;
		static final Predicate<Annotation[]> HAS_ONLY_ONE_REQUEST_INJECTION = new Predicate<Annotation[]>() {
			private Predicate<Annotation> isRequestInjection = new Predicate<Annotation>() {
				@Override
				public boolean apply(Annotation annotation) {
					return annotation.annotationType().isAnnotationPresent(RequestInjection.class);
				}
			};

			@Override
			public boolean apply(Annotation[] annotations) {
				return size(
						filter(
								asList(annotations),
								isRequestInjection
						)
				) == 1;
			}
		};

		static <T> Iterable<Field> getTargetFieldsFromClass(Class<T> targetClass) {
			return filter(
					asList(targetClass.getFields()),
					new Predicate<Field>() {
						@Override
						public boolean apply(Field field) {
							return HAS_ONLY_ONE_REQUEST_INJECTION.apply(field.getAnnotations());
						}
					}
			);
		}

	}
}
