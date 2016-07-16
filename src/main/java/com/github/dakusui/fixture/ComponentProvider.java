package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.Provider;

public class ComponentProvider extends Provider.Base<Component> {
	@Override
	protected Component instanceFor(Context context, InjectionRequest request) {
		return new Component() {
		};
	}
}
