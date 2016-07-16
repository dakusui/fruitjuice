package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.RequestInjection;
import com.google.common.base.Throwables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Fixture {
	@interface Value {
		String name();

		String value();
	}

	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@RequestInjection(provider = ComponentProvider.class)
	@interface InjectComponent {
		String type();

		Value[] config() default {};
	}

	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@RequestInjection(provider = SubsystemProvider.class)
	@interface InjectSubsystem {
		String type();

		Value[] config() default {};

		Value[] dependencies() default {};

		Value[] childComponents() default {};
	}

	enum Factory {
		;

		static <T extends Fixture> T create(Class<T> fixtureClass) {
			Context context = new Context() {
				@Override
				public <U> U resolve(InjectionRequest request) {
					try {
						return (U) request.getType().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw Throwables.propagate(e);
					}
				}
			};
			return FruitJuice
					.createInjector(context)
					.getInstance(checkNotNull(fixtureClass));
		}
	}
}