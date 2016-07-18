package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.ValueFactory;

public class ComponentFactory implements ValueFactory {
  @Override
  public Object create(final InjectionRequest request) {
    return new Component() {
      @Override
      public String toString() {
        return "component:" + request.getName();
      }
    };
  }
}
