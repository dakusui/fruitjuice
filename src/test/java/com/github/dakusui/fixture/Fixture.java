package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.RequestInjection;
import com.google.common.base.Preconditions;

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

  interface Attribute {
    <T> T apply();

    class Basic implements Attribute {
      private final Value value;

      public Basic(Value value) {
        this.value = checkNotNull(value);
      }

      @Override
      public <T> T apply() {
        return (T) value.value();
      }
    }

    interface Bundle {
      Attribute config(String name);

      String type();

      class Basic implements Bundle {
        private final Value[] configs;
        private final Value[] dependencies;
        private final Value[] childComponents;
        private final String type;

        public Basic(String type, Value[] configs, Value[] dependencies, Value[] childComponents) {
          this.type = type;
          this.configs = configs;
          this.dependencies = dependencies;
          this.childComponents = childComponents;
        }

        @Override
        public Attribute config(String name) {
          Preconditions.checkNotNull(name);
          if (this.configs == null) {
            throw new UnsupportedOperationException(format("'%s' does not support this operation.", this.toString()));
          }
          for (Value each : this.configs) {
            if (name.equals(each.name()))
              return new Attribute.Basic(each);
          }
          throw new IllegalArgumentException(format("Attribute '%s' was not found in '%s'", name, this.toString()));
        }

        @Override
        public String type() {
          return this.type;
        }
      }

      enum Factory {
        ;

        public static Bundle create(InjectComponent ann) {
          return new Bundle.Basic(ann.type(), ann.config(), null, null);
        }

        public static Bundle create(InjectSubsystem ann) {
          return new Bundle.Basic(ann.type(), ann.config(), ann.dependencies(), ann.childComponents());
        }

        public static Bundle create(InjectFixture component) {
          return new Bundle.Basic(null, null, null, null);
        }
      }
    }
  }

  enum Factory {
    ;

    static <T extends Fixture> T create(Class<T> fixtureClass) {
      return FruitJuice.createInjector(
          new Context.Builder.Base() {
            @Override
            protected Object create(final InjectionRequest request) {
              final Attribute.Bundle attributeBundle = createAttributeBundleFromInjectionRequest(request);
              if (request.isAnnotationPresent(InjectComponent.class)) {
                return new Component() {
                  @Override
                  public String toString() {
                    return "component:" +  attributeBundle.type() + "(" + formatValues(request.getAnnotation(InjectComponent.class).config()) + ")";
                  }
                };
              } else if (request.isAnnotationPresent(InjectSubsystem.class)) {
                return new Subsystem() {
                  @Override
                  public String toString() {
                    return "subsystem:" + attributeBundle.type() + "(" + formatValues(request.getAnnotation(InjectSubsystem.class).config()) + ")";
                  }
                };
              } else if (request.isAnnotationPresent(InjectFixture.class)) {
                return FruitJuice.createInjector(this).getInstance(ExampleFixture.NestedFixture.class);
              }
              throw new RuntimeException(format("Unsupported request: %s", request));
            }

            Attribute.Bundle createAttributeBundleFromInjectionRequest(InjectionRequest request) {
              if (request.isAnnotationPresent(InjectComponent.class)) {
                return Attribute.Bundle.Factory.create(request.getAnnotation(InjectComponent.class));
              } else if (request.isAnnotationPresent(InjectSubsystem.class)) {
                return Attribute.Bundle.Factory.create(request.getAnnotation(InjectSubsystem.class));
              } else if (request.isAnnotationPresent(InjectFixture.class)) {
                return Attribute.Bundle.Factory.create(request.getAnnotation(InjectFixture.class));
              }
              throw new RuntimeException(format("Unsupported request: %s", request));
            }

            String formatValues(Value[] values) {
              StringBuilder b = new StringBuilder();
              boolean first = true;
              for (Value each : values) {
                if (!first)
                  b.append(",");
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