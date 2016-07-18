package com.github.dakusui.fruitjuice;

public class FruitJuice {
	public static Injector createInjector(Context.Builder builder) {
		return new Injector.Impl(builder);
	}
}
