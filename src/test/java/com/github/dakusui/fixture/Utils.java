package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public enum Utils {
  ;

  static String formatValues(Fixture.Value[] values) {
    StringBuilder b = new StringBuilder();
    boolean first = true;
    for (Fixture.Value each : values) {
      if (!first)
        b.append(",");
      b.append(format("%s=%s", each.name(), each.value()));
      first = false;
    }
    return b.toString();
  }

  static Attribute.Bundle createAttributeBundleFromInjectionRequest(InjectionRequest request) {
    checkNotNull(request);
    return InjectionType.typeOf(request).createAttributeBundle(request);
  }
}
