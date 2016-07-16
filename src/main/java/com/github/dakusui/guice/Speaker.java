package com.github.dakusui.guice;

import com.google.inject.ImplementedBy;

@ImplementedBy(JapaneseSpeaker.class)
public interface Speaker {
	void thankYou();
}
