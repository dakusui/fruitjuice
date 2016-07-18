package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.ValueFactory;

public class SubsystemFactory implements ValueFactory {
  @Override
  public Object create(InjectionRequest request) {
    return "subsystem:" + request.getName();
  }
}
