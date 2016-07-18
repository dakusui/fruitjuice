package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Fixture {
  @interface Value {
    String name();

    String value();
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @RequestInjection(ComponentFactory.class)
  @interface InjectComponent {
    String type();

    Value[] config() default {};
  }

  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @Retention(RetentionPolicy.RUNTIME)
  @RequestInjection(SubsystemFactory.class)
  @interface InjectSubsystem {
    String type();

    Value[] config() default {};

    Value[] dependencies() default {};

    Value[] childComponents() default {};
  }

  enum Factory {
    ;

    static <T extends Fixture> T create(Class<T> fixtureClass) {
      return FruitJuice
          .createInjector(new Context.Builder() {
            Map<InjectionRequest, Object> registry = new HashMap<>();

            @Override
            public Context.Builder add(InjectionPoint injectionPoint) {
              registry.put(
                  injectionPoint.getRequest(),
                  injectionPoint.getValueFactory().create(
                      injectionPoint.getRequest()
                  )
              );
              return this;
            }

            @Override
            public Context build() {
              return new Context() {
                @Override
                public <T> T lookup(InjectionRequest request) {
                  return (T) registry.get(request);
                }
              };
            }
          })
          .getInstance(checkNotNull(fixtureClass));
    }
  }
}