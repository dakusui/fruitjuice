package com.github.dakusui.example.tests;

import com.github.dakusui.example.fixture.ExampleFixture;
import com.github.dakusui.example.fixture.Fixture;
import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.FruitJuice;
import com.github.dakusui.fruitjuice.InjectionRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixtureTest {
  @Test
  public void whenInjectFixture$thenInjectionHappensAppropriately() {
    ExampleFixture fixture = Fixture.Factory.create(ExampleFixture.class);
    ////
    // Field injection happens correctly
    assertEquals(
        "subsystem:CASSANDRA(configs={config1=configvalue1},dependencies={dep1=depvalue1,dep2=depvalue2},children={child1=childvalue1,child2=childvalue2})",
        fixture.subsystem.toString()
    );
    ////
    // Field injection happens correctly. And make sure request can be dispatched.
    assertEquals(
        "component:ADMIN({adminSlot=testenv101:0})",
        fixture.getAdmin().toString()
    );
    ////
    // Constructor injection happens correctly.
    assertEquals(
        "component:DATALOADER({})",
        fixture.getDataloader().toString()
    );
    ////
    // Nested injection happens correctly.
    assertEquals(
        "component:NGINX({})",
        fixture.getNestedFixture().nginx.toString()
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
}
