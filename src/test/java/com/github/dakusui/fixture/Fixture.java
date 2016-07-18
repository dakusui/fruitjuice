package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.RequestInjection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

interface Fixture {
  @interface Value {
    String name();

    String value();
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @RequestInjection
  @interface InjectFixture {
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @RequestInjection
  @interface InjectComponent {
    String type();

    Value[] config() default {};
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @RequestInjection
  @interface InjectSubsystem {
    String type();

    Value[] config() default {};

    Value[] dependencies() default {};

    Value[] childComponents() default {};
  }

  enum Factory {
    ;

    static <T extends Fixture> T create(Class<T> fixtureClass) {
      return FruitJuice.createInjector(
          new Context.Builder.Base() {
            @Override
            protected Object create(final InjectionRequest request) {
              if (request.isAnnotationPresent(InjectComponent.class)) {
                return new Component() {
                  @Override
                  public String toString() {
                    return "component:" + request.getName() + "(" + formatValues(request.getAnnotation(InjectComponent.class).config()) + ")";
                  }
                };
              } else if (request.isAnnotationPresent(InjectSubsystem.class)) {
                return new Subsystem() {
                  @Override
                  public String toString() {
                    return "subsystem:" + request.getName() + "(" + formatValues(request.getAnnotation(InjectSubsystem.class).config()) + ")";
                  }
                };
              } else if (request.isAnnotationPresent(InjectFixture.class)) {
                return FruitJuice.createInjector(this).getInstance(ExampleFixture.NestedFixture.class);
              }
              throw new RuntimeException(format("Unsupported request: %s", request));
            }

            String formatValues(Value[] values) {
              StringBuilder b = new StringBuilder();
              boolean first = true;
              for (Value each : values) {
                if (!first) b.append(",");
                b.append(each.name());
                b.append("=");
                b.append(each.value());
                first = false;
              }
              return b.toString();
            }
          }).getInstance(checkNotNull(fixtureClass));
    }
  }
}