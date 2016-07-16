package com.github.dakusui.fixture;

import com.github.dakusui.fruitjuice.Context;
import com.github.dakusui.fruitjuice.InjectionRequest;
import com.github.dakusui.fruitjuice.Provider;

public class SubsystemProvider extends Provider.Base<Subsystem> {
	@Override
	protected Subsystem instanceFor(Context context, InjectionRequest request) {
		return new Subsystem() {
		};
	}
}
