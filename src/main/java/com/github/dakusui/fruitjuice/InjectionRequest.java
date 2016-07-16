package com.github.dakusui.fruitjuice;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.google.common.collect.Iterables.isEmpty;

public interface InjectionRequest {
	boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

	<T extends Annotation> T getAnnotation(Class<T> annotationClass);

	Class<?> getType();

	String getName();

	Iterable<Annotation> getAnnotations();

	abstract class Base implements InjectionRequest {
		private final Iterable<Annotation> annotations;

		protected Base(Annotation[] annotations) {
			this.annotations = Iterables.filter(
					Arrays.asList(annotations),
					new Predicate<Annotation>() {
						@Override
						public boolean apply(Annotation annotation) {
							return annotation.annotationType().isAnnotationPresent(RequestInjection.class);
						}
					}
			);
			Preconditions.checkState(!isEmpty(this.annotations));
		}

		@Override
		public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
			return getAnnotation(annotationClass) != null;
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
			//noinspection unchecked
			return (T) Iterables.find(this.annotations, new Predicate<Annotation>() {
				@Override
				public boolean apply(Annotation annotation) {
					return annotationClass.isInstance(annotation);
				}
			});
		}

		@Override
		public Iterable<Annotation> getAnnotations() {
			return this.annotations;
		}
	}

	enum Factory {
		;

		static InjectionRequest createFromField(final Field field) {
			Preconditions.checkNotNull(field);
			return new InjectionRequest.Base(field.getAnnotations()) {
				@Override
				public Class<?> getType() {
					return field.getType();
				}

				@Override
				public String getName() {
					return field.getName();
				}
			};
		}

		public static InjectionRequest createFromParameter(final Class parameterType, Annotation[] annotations, final String name) {
			return new InjectionRequest.Base(annotations) {
				@Override
				public Class<?> getType() {
					return parameterType;
				}

				@Override
				public String getName() {
					return name;
				}
			};
		}
	}
}
