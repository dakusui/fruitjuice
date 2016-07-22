package com.github.dakusui.fruitjuice.tests;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionPoint;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.tests.fixture.ExampleFixture;
import com.github.dakusui.fruitjuice.tests.fixture.Fixture;
import com.github.dakusui.fruitjuice.tests.fixture.InjectionType;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class FixtureTest {
  @Test
  public void whenInjectFixture$thenInjectionHappensAppropriately() {
    ExampleFixture fixture = Fixture.Factory.create(ExampleFixture.class);
    ////
    // Field injection happens correctly
    assertEquals(
        "subsystem:TROJAN(configs={config1=configvalue1},dependencies={dep1=depvalue1,dep2=depvalue2},children={child1=childvalue1,child2=childvalue2})",
        fixture.trojan.toString()
    );
    ////
    // Field injection happens correctly. And make sure request can be dispatched.
    assertEquals(
        "component:PATROCLUS({controller=hostname:80})",
        fixture.getPatroclus().toString()
    );
    ////
    // Constructor injection happens correctly.
    assertEquals(
        "component:HECTOR({})",
        fixture.getHector().toString()
    );
    ////
    // Constructor injection happens correctly for strings
    assertEquals(
        "HelloSystem",
        fixture.getMessage()
    );

    ////
    // Nested injection happens correctly.
    assertEquals(
        "component:PRIAMOS({})",
        fixture.getNestedFixture().priamos.toString()
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenInjectFixtureWithContextThatGivesInvalidObjects$thenExceptionWillBeThrown() {
    ExampleFixture fixture = FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      protected Object create(InjectionRequest request) {
        ////
        // An exception is an invalid to return, because we know that there is
        // no injection point in ExampleFixture whose type is Exception.
        return new Exception();
      }
    }).getInstance(ExampleFixture.class);
    ////
    // This path shouldn't be executed as long as some exception is thrown from Fruit
    // juice.
    System.err.println(fixture);
  }

  @Test
  public void whenInjectFixture$thenMethodsOfConstructorParameterAreAvailable() {
    final Set<InjectionPoint.ConstructorParameter> constructorParameters = new HashSet<>();
    FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      public Context.Builder add(InjectionPoint injectionPoint) {
        if (injectionPoint.getTargetElement().getType() == InjectionPoint.Type.CONSTRUCTOR_PARAMETER) {
          final InjectionPoint.ConstructorParameter param = injectionPoint.getTargetElement().asConstructorParameter();
          ////
          // path-1: make sure getModifiers works correct.
          assertTrue(Modifier.isPublic(param.getModifiers()));
          assertNotNull(param.getType());
          assertThat(param.getIndex(), anyOf(equalTo(0), equalTo(1)));

          ////
          //intentional
          //noinspection EqualsWithItself
          assertTrue(param.equals(param));
          assertFalse(param.equals(createDummyConstructorParameter(param)));
          ////
          //intentional
          //noinspection EqualsBetweenInconvertibleTypes
          assertFalse(param.equals("Dummy object"));
          constructorParameters.add(param);
        }
        return super.add(injectionPoint);
      }

      @Override
      protected Object create(InjectionRequest request) {
        checkNotNull(request);
        return InjectionType.typeOf(request).create(this, request);
      }

      private InjectionPoint.ConstructorParameter createDummyConstructorParameter(final InjectionPoint.ConstructorParameter param) {
        return new InjectionPoint.ConstructorParameter() {
          @Override
          public Class<?> getType() {
            return param.getType();
          }

          @Override
          public Constructor getDeclaringConstructor() {
            return param.getDeclaringConstructor();
          }

          @Override
          public int getIndex() {
            return -1;
          }

          @Override
          public int getModifiers() {
            return param.getModifiers();
          }
        };
      }

    }).getInstance(ExampleFixture.class);
    ///
    // Make sure path-1 was executed.
    assertEquals(2, constructorParameters.size());
  }

  @Test
  public void whenInjectFixture$thenRequestsCanBeStringified() {
    FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      protected Object create(InjectionRequest request) {
        checkNotNull(request);
        assertThat(request.toString(),
            anyOf(
                CoreMatchers.startsWith("Subsystem:"),
                CoreMatchers.startsWith("Component:"),
                CoreMatchers.startsWith("String:"),
                CoreMatchers.startsWith("Fixture:")
            )
        );
        ////
        //intentional. To test InjectRequest#equals
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(request.equals(""));
        return InjectionType.typeOf(request).create(this, request);
      }
    }).getInstance(ExampleFixture.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void givenInjectFixture$whenAsConstructorParameterIsCalledOnFieldInjectionPoint$thenExceptionThrown() {
    FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      public Context.Builder add(InjectionPoint injectionPoint) {
        if (injectionPoint.getTargetElement().getType() == InjectionPoint.Type.FIELD) {
          injectionPoint.getTargetElement().asConstructorParameter();
        }
        return super.add(injectionPoint);
      }

      @Override
      protected Object create(InjectionRequest request) {
        checkNotNull(request);
        return InjectionType.typeOf(request).create(this, request);
      }
    }).getInstance(ExampleFixture.class);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void givenInjectFixture$whenAsFieldIsCalledOnConstructorParameterInjectionPoint$thenExceptionThrown() {
    FruitJuice.createInjector(new Context.Builder.Base() {
      @Override
      public Context.Builder add(InjectionPoint injectionPoint) {
        if (injectionPoint.getTargetElement().getType() == InjectionPoint.Type.CONSTRUCTOR_PARAMETER) {
          injectionPoint.getTargetElement().asField();
        }
        return super.add(injectionPoint);
      }

      @Override
      protected Object create(InjectionRequest request) {
        checkNotNull(request);
        return InjectionType.typeOf(request).create(this, request);
      }
    }).getInstance(ExampleFixture.class);
  }
}
