package com.github.dakusui;

import com.github.dakusui.guice.English;
import com.github.dakusui.guice.EnglishSpeaker;
import com.github.dakusui.guice.GuiceMain;
import com.github.dakusui.guice.Japanese;
import com.github.dakusui.guice.JapaneseSpeaker;
import com.github.dakusui.guice.Speaker;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import static com.google.inject.name.Names.named;

/**
 * Unit test for simple App.
 */
public class AppTest {
	@Test
	public void test1() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			protected void configure() {
			}
		});

		GuiceMain main = injector.getInstance(GuiceMain.class);
		main.getSpeaker().thankYou();
	}

	@Test
	public void test2() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			protected void configure() {
				bind(Speaker.class).annotatedWith(Japanese.class).to(JapaneseSpeaker.class);
				bind(Speaker.class).annotatedWith(English.class).to(EnglishSpeaker.class);
			}
		});

		GuiceMain main = injector.getInstance(GuiceMain.class);
		main.getSpeaker().thankYou();
		main.japaneseSpeaker.thankYou();
		main.englishSpeaker.thankYou();
	}


	@Test
	public void test3() {
		Injector injector = Guice.createInjector(new AbstractModule() {
			protected void configure() {
				bind(Speaker.class)
						.annotatedWith(named(""))
						.to(JapaneseSpeaker.class);
			}
		});

		GuiceMain main = injector.getInstance(GuiceMain.class);
		main.getSpeaker().thankYou();
		main.japaneseSpeaker.thankYou();
		main.englishSpeaker.thankYou();
	}
}
