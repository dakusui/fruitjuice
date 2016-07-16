package com.github.dakusui.fruitjuice;

import java.util.HashMap;
import java.util.Map;

public interface Context {
  void prepare(InjectionPoint injectionPoint);

  Object lookup(InjectionRequest request);

  abstract class Base implements Context {
    Map<InjectionRequest, Object> registry = new HashMap<>();

    @Override
    public void prepare(InjectionPoint injectionPoint) {
      registry.put(
          injectionPoint.getRequest(),
          injectionPoint.getProvider().getInstanceFor(
              injectionPoint.getRequest(),
              this
          )
      );

    }

    @Override
    public Object lookup(InjectionRequest request) {
      return registry.get(request);
    }
  }
}
