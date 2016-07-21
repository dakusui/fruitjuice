package com.github.dakusui.fruitjuice;

/**
 * The entry point to the FruitJuice framework. Creates {@link Injector}s from
 * {@link com.github.dakusui.fruitjuice.Context.Builder}.
 *
 * Following is a code example of FruitJuice.
 * <pre>
 *     public class FooApplication {
 *       public static void main(String[] args) {
 *         Injector injector = FruitJuice.createInjector(
 *             new Context.Builder.Base() {
 *               protected Object create(InjectionRequest request) {
 *                 ...
 *               }
 *             }
 *         );
 *         // Now just bootstrap the application and you're done
 *         FooStarter starter = injector.getInstance(FooStarter.class);
 *         starter.runApplication();
 *       }
 *     }
 * </pre>
 *
 * @see InjectionPoint
 * @see InjectionRequest
 * @see Context
 * @see Context.Builder
 * @see Context.Builder.Base
 */
public enum FruitJuice {
  ;

  public static Injector createInjector(Context.Builder builder) {
    return new Injector.Impl(builder);
  }
}
