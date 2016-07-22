package com.github.dakusui.example.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;

import static com.github.dakusui.example.fixture.Utils.createAttributeBundleFromInjectionRequest;
import static com.github.dakusui.example.fixture.Utils.formatValues;
import static java.lang.String.format;

public interface Subsystem {
  enum Factory {
    ;

    static Subsystem create(final InjectionRequest request) {
      final Attribute.Bundle attributeBundle = createAttributeBundleFromInjectionRequest(request);
      return new Subsystem() {
        @Override
        public String toString() {
          return "subsystem:" + attributeBundle.type() + "(" +
              format("configs={%s},", formatValues(request.getAnnotation(Fixture.InjectSubsystem.class).config())) +
              format("dependencies={%s},", formatValues(request.getAnnotation(Fixture.InjectSubsystem.class).dependencies())) +
              format("children={%s}", formatValues(request.getAnnotation(Fixture.InjectSubsystem.class).childComponents())) +
              ")";
        }
      };
    }
  }
}

