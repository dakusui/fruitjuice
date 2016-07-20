package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;

import static java.lang.String.format;

public enum InjectionType {
  COMPONENT {
    @Override
    Object create(Context.Builder builder, InjectionRequest request) {
      return Component.Factory.create(request);
    }

    @Override
    Attribute.Bundle createAttributeBundle(InjectionRequest request) {
      Fixture.InjectComponent ann = request.getAnnotation(Fixture.InjectComponent.class);
      return new Attribute.Bundle.Basic(ann.type(), ann.config(), null, null);
    }
  },
  SUBSYSTEM {
    @Override
    Object create(Context.Builder builder, InjectionRequest request) {
      return Subsystem.Factory.create(request);
    }

    @Override
    Attribute.Bundle createAttributeBundle(InjectionRequest request) {
      Fixture.InjectSubsystem ann = request.getAnnotation(Fixture.InjectSubsystem.class);
      return new Attribute.Bundle.Basic(ann.type(), ann.config(), ann.dependencies(), ann.childComponents());
    }
  },
  NESTED_FIXTURE {
    @Override
    Object create(Context.Builder builder, InjectionRequest request) {
      return FruitJuice.createInjector(builder).getInstance(ExampleFixture.NestedFixture.class);
    }

    @Override
    Attribute.Bundle createAttributeBundle(InjectionRequest request) {
      return new Attribute.Bundle.Basic(null, null, null, null);
    }
  },
  SYSTEM_NAME {
    @Override
    Object create(Context.Builder builder, InjectionRequest request) {
      return "HelloSystem";
    }

    @Override
    Attribute.Bundle createAttributeBundle(InjectionRequest request) {
      return new Attribute.Bundle.Basic(null, null, null, null);
    }
  },;

  abstract Object create(Context.Builder builder, InjectionRequest request);

  abstract Attribute.Bundle createAttributeBundle(InjectionRequest request);

  static InjectionType typeOf(InjectionRequest request) {
    if (request.isAnnotationPresent(Fixture.InjectComponent.class)) {
      return COMPONENT;
    } else if (request.isAnnotationPresent(Fixture.InjectSubsystem.class)) {
      return SUBSYSTEM;
    } else if (request.isAnnotationPresent(Fixture.InjectFixture.class)) {
      return NESTED_FIXTURE;
    } else if (request.getType().equals(String.class)) {
      return SYSTEM_NAME;
    }
    throw new RuntimeException(format("Unsupported request: %s", request));
  }
}
