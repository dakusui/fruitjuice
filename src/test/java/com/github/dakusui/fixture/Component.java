package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;

import static com.github.dakusui.fixture.Utils.createAttributeBundleFromInjectionRequest;
import static com.github.dakusui.fixture.Utils.formatValues;

public interface Component {
  enum Factory {
    ;
    static Component create(final InjectionRequest request) {
      final Attribute.Bundle attributeBundle = createAttributeBundleFromInjectionRequest(request);
      return new Component() {
        @Override
        public String toString() {
          return "component:" + attributeBundle.type() + "(" + formatValues(request.getAnnotation(Fixture.InjectComponent.class).config()) + ")";
        }
      };
    }
  }
}
