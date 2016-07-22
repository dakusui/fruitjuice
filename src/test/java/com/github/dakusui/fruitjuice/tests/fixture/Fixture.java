package com.github.dakusui.example.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;

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

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @interface InjectFixture {
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @interface InjectComponent {
    String type();

    Value[] config() default {};
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @interface InjectSubsystem {
    String type();

    Value[] config() default {};

    Value[] dependencies() default {};

    Value[] childComponents() default {};
  }

  enum Factory {
    ;

    public static <T extends Fixture> T create(Class<T> fixtureClass) {
      return FruitJuice.createInjector(
          new Context.Builder.Base() {
            @Override
            protected Object create(final InjectionRequest request) {
              checkNotNull(request);
              return InjectionType.typeOf(request).create(this, request);
            }
          }).getInstance(checkNotNull(fixtureClass));
    }
  }
}