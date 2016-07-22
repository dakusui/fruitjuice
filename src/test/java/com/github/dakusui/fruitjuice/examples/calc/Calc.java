package com.github.dakusui.fruitjuice.examples.calc;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.Inject;
import com.github.dakusui.fruitjuice.InjectionRequest;

public class Calc {
  @Inject
  @Add({ 1, 2, 3 })
  public int sum;

  public static void main(String... args) {
    int result = FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      protected Object create(InjectionRequest request) {
        int ret = 0;
        for (int each : request.getAnnotation(Add.class).value()) {
          ret += each;
        }
        return ret;
      }
    }).getInstance(Calc.class).sum;
    System.out.println(result);
  }
}
