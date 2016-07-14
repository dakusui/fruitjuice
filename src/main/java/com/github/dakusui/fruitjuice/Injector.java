package com.github.dakusui.fruitjuice;

public interface Injector {
  <T> T getInstance(Class<T> type);
}
