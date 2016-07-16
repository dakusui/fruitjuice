package com.github.dakusui.fruitjuice;

import com.google.common.base.Preconditions;

public interface Provider<T> {
	/**
	 * Returns an instance to be injected to a given {@code field}.
	 *
	 * @param request A field to which the returned instance is injected.
	 */
	T getInstanceFor(InjectionRequest request, Context context);

	abstract class Base<T> implements Provider<T> {
		@Override
		final public T getInstanceFor(InjectionRequest request, Context context) {
			Preconditions.checkNotNull(request);
			Preconditions.checkNotNull(context);
			return instanceFor(context, request);
		}

		protected abstract T instanceFor(Context context, InjectionRequest field) ;
	}
}
