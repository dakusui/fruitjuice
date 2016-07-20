package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.UseForInjection;

public class ExampleFixture implements Fixture {
  public static class NestedFixture implements Fixture {
    @InjectComponent(
        type = "NGINX",
        config = {}
    )
    public Component nginx;

    public String toString() {
      return String.format("nginx=%s", this.nginx);
    }
  }

  @InjectFixture
  Fixture nestedFixture;

  @InjectComponent(
      type = "ADMIN",
      config = {@Value(name="adminSlot", value = "testenv101:0")}
  )
  private Component admin;

  @InjectSubsystem(
      type = "CASSANDRA",
      config = {},
      dependencies = {},
      childComponents = {}
  )
  public Subsystem subsystem;

  @UseForInjection
  public ExampleFixture(
      @InjectComponent(type = "DATALOADER", config = {}) Component dataloader
  ) {
    System.out.println(dataloader);
  }

  public String toString() {
    return String.format("admin=%s; subsystem=%s; nested=[%s]", this.admin, this.subsystem, this.nestedFixture);
  }

  public static ExampleFixture create() {
    return Fixture.Factory.create(ExampleFixture.class);
  }

  public static void main(String... args) {
    System.out.println(create());
  }
}
