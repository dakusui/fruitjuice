package com.github.dakusui.fruitjuice;

public interface Context {
  <T> T lookup(InjectionRequest request);

  interface Builder {
    Builder add(InjectionPoint injectionPoint);
    Context build();
  }
}
