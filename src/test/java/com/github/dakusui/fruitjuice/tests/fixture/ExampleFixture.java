package com.github.dakusui.fruitjuice.tests.fixture;

import com.github.dakusui.fruitjuice.Inject;

public class ExampleFixture implements Fixture {
  public static class NestedFixture implements Fixture {
    @Inject
    @InjectComponent(
        type = "PRIAMOS",
        config = {}
    )
    public Component priamos;
  }

  @Inject
  @InjectFixture
  Fixture nestedFixture;


  @Inject
  @InjectSubsystem(
      type = "TROJAN",
      config = {@Value(name="config1", value="configvalue1")},
      dependencies = {
          @Value(name="dep1", value="depvalue1"),
          @Value(name="dep2", value="depvalue2")
      },
      childComponents = {
          @Value(name="child1", value="childvalue1"),
          @Value(name="child2", value="childvalue2")
      }
  )
  public Subsystem trojan;

  @Inject
  @InjectComponent(
      type = "PATROCLUS",
      config = {@Value(name="controller", value = "hostname:80")}
  )
  private Component patroclus;

  private final Component hector;

  private final String message;

  @Inject
  public ExampleFixture(
      @InjectComponent(type = "HECTOR", config = {}) Component hector,
      String message
  ) {
    this.message = message;
    this.hector = hector;
  }

  public NestedFixture getNestedFixture() {
    return (NestedFixture) this.nestedFixture;
  }

  public Component getPatroclus() {
    return patroclus;
  }

  public Component getHector() {
    return this.hector;
  }

  public String getMessage() {
    return this.message;
  }

  public String toString() {
    return String.format("patroclus=%s; subsystem=%s; nested=[%s]", this.patroclus, this.trojan, this.nestedFixture);
  }
}
