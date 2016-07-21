package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;

import static com.github.dakusui.fixture.Utils.createAttributeBundleFromInjectionRequest;
import static com.github.dakusui.fixture.Utils.formatValues;

public interface Subsystem {
  enum Factory {
    ;
    static Subsystem create(final InjectionRequest request) {
      final Attribute.Bundle attributeBundle = createAttributeBundleFromInjectionRequest(request);
      return new Subsystem() {
        @Override
        public String toString() {
          return "subsystem:" + attributeBundle.type() + "(" + formatValues(request.getAnnotation(Fixture.InjectSubsystem.class).config()) + ")";
        }
      };
    }
  }
}

