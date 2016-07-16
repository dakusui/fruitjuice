package com.github.dakusui.fruitjuice;

public interface ProviderRegistry {
	void register(Provider<?> provider);

	<T extends Provider<?>> Provider<?> lookup(Class<T> providerClass);

	class Impl implements ProviderRegistry {
		@Override
		public void register(Provider<?> provider) {

		}

		@Override
		public <T extends Provider<?>> Provider<?> lookup(Class<T> providerClass) {
			return null;
		}
	}
}
