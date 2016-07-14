package com.github.dakusui.fruitjuice;

public class FruitJuice {
  public interface Context {
  }

  public static Injector createInjector(final Context context) {
    return new Injector() {
      public <T> T getInstance(Class<T> type) {
        return null;
      }
    };
  }
}
