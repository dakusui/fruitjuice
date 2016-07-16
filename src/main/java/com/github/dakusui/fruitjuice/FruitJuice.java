package com.github.dakusui.fruitjuice;

public class FruitJuice {
	public static Injector createInjector(Context context) {
		return new Injector.Impl(context);
	}
}
