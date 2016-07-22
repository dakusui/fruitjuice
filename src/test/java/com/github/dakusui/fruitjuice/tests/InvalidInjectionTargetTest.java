package com.github.dakusui.fruitjuice.tests;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.Inject;
import com.github.dakusui.fruitjuice.InjectionRequest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InvalidInjectionTargetTest {
  public static class NoAvailableConstructor {
    private NoAvailableConstructor() {
    }
  }

  @Test(expected = RuntimeException.class)
  public void givenNoAvailableConstructorTarget$whenInjectionHappens$thenExceptionReported() {
    try {
      FruitJuice.createInjector(new Context.Builder.Base() {
        @Override
        protected Object create(InjectionRequest request) {
          return null;
        }
      }).getInstance(NoAvailableConstructor.class);
    } catch (RuntimeException e) {
      assertThat(
          e.getMessage(),
          startsWith("No available constructor for injection is found in")
      );
      throw e;
    }
  }

  public static class MultipleAnnotatedConstructors {
    @Inject
    public MultipleAnnotatedConstructors() {
    }

    @Inject
    public MultipleAnnotatedConstructors(int i) {
    }
  }

  @Test(expected = RuntimeException.class)
  public void givenMultipleConstructors$whenInjectionHappens$thenExceptionReported() {
    try {
      FruitJuice.createInjector(new Context.Builder.Base() {
        @Override
        protected Object create(InjectionRequest request) {
          return null;
        }
      }).getInstance(MultipleAnnotatedConstructors.class);
    } catch (RuntimeException e) {
      assertThat(
          e.getMessage(),
          startsWith("More than one constructors annotated with")
      );
      throw e;
    }
  }

  public static class FailingConstructor {
    public FailingConstructor() {
      throw new RuntimeException("FAIL!");
    }
  }

  @Test(expected = RuntimeException.class)
  public void givenFailingConstructor$whenInjectionHappens$thenExceptionReported() {
    try {
      FruitJuice.createInjector(new Context.Builder.Base() {
        @Override
        protected Object create(InjectionRequest request) {
          return null;
        }
      }).getInstance(FailingConstructor.class);
    } catch (RuntimeException e) {
      assertEquals("FAIL!", e.getMessage());
      throw e;
    }
  }

  public static abstract class AbstractClass {
  }

  @Test(expected = RuntimeException.class)
  public void givenAbstractClass$whenInjectionHappens$thenExceptionReported() {
    FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      protected Object create(InjectionRequest request) {
        return null;
      }
    }).getInstance(AbstractClass.class);
  }

  public static class FinalFieldAsInjectionPoint {
    @Inject
    public final        String test1 = "hello";
    @Inject
    public static       String test2 = "hello";
    @Inject
    public final static String test3 = "hello";
  }


  @Test(expected = IllegalArgumentException.class)
  public void givenFinalFieldAsInjectionPoint$whenInjectionHappens$thenExceptionReported() {
    try {
      FruitJuice.createInjector(new Context.Builder.Base() {
        @Override
        protected Object create(InjectionRequest request) {
          return "Bye";
        }
      }).getInstance(FinalFieldAsInjectionPoint.class);
    } catch (RuntimeException e) {
      assertThat(
          e.getMessage(),
          allOf(
              containsString("Field 'test1' is marked final"),
              containsString("Field 'test2' is marked static"),
              containsString("Field 'test3' is marked final"),
              containsString("Field 'test3' is marked static")
          )
      );
      throw e;
    }
  }
}
