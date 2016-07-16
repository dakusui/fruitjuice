package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.UseForInjection;

public class ExampleFixture implements Fixture {
	@InjectComponent(
			type = "ADMIN",
			config = {}
	)
	public Component admin;

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

	public static ExampleFixture create() {
		return Fixture.Factory.create(ExampleFixture.class);
	}

	public static void main(String... args) {
		System.out.println(create());
	}
}
