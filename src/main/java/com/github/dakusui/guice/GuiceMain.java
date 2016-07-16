package com.github.dakusui.guice;

import com.google.inject.Inject;

public class GuiceMain {
	@Inject @Japanese
	public
	Speaker japaneseSpeaker;

	@Inject @English
	public
	Speaker englishSpeaker;

	@Inject
	Speaker speaker;

	public Speaker getSpeaker() {
		return this.speaker;
	}
}

