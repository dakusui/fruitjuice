package com.github.dakusui.fruitjuice.examples.executorservice;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.Inject;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.google.common.base.Throwables;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContainsMultiThreadedExecutorService {
  private final ExecutorService executorService;

  @Inject
  public ContainsMultiThreadedExecutorService(@MultiThreaded(poolSize = 4) ExecutorService executorService) {
    this.executorService = executorService;
  }

  public static void main(String... args) {
    ContainsMultiThreadedExecutorService injected = FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      protected Object create(InjectionRequest request) {
        return Executors.newFixedThreadPool(request.getAnnotation(MultiThreaded.class).poolSize());
      }
    }).getInstance(ContainsMultiThreadedExecutorService.class);

    injected.executorService.execute(createRunnable("matthew"));
    injected.executorService.execute(createRunnable("mark"));
    injected.executorService.execute(createRunnable("luke"));
    injected.executorService.execute(createRunnable("john"));
    injected.executorService.shutdown();
  }

  private static Runnable createRunnable(final String message) {
    return new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 100; i++) {
          System.out.println(message);
          try {
            TimeUnit.MILLISECONDS.sleep(10);
          } catch (InterruptedException e) {
            throw Throwables.propagate(e);
          }
        }
      }
    };
  }
}
