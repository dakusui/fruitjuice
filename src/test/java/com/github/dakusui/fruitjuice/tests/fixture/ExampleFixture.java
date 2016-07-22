package com.github.dakusui.example.fixture;

import com.github.dakusui.fruitjuice.Inject;

public class ExampleFixture implements Fixture {
  private final String msg;
  private final Component dataloader;

  public Component getAdmin() {
    return admin;
  }

  public static class NestedFixture implements Fixture {
    @Inject
    @InjectComponent(
        type = "NGINX",
        config = {}
    )
    public Component nginx;

    public String toString() {
      return String.format("nginx=%s", this.nginx);
    }
  }

  @Inject
  @InjectFixture
  Fixture nestedFixture;

  @Inject
  @InjectComponent(
      type = "ADMIN",
      config = {@Value(name="adminSlot", value = "testenv101:0")}
  )
  private Component admin;

  @Inject
  @InjectSubsystem(
      type = "CASSANDRA",
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
  public Subsystem subsystem;

  @Inject
  public ExampleFixture(
      @InjectComponent(type = "DATALOADER", config = {}) Component dataloader,
      String msg
  ) {
    this.msg = msg;
    this.dataloader = dataloader;
  }

  public NestedFixture getNestedFixture() {
    return (NestedFixture) this.nestedFixture;
  }

  public Component getDataloader() {
    return this.dataloader;
  }

  public String toString() {
    return String.format("admin=%s; subsystem=%s; nested=[%s]", this.admin, this.subsystem, this.nestedFixture);
  }
}
