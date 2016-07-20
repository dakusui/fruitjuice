package com.github.dakusui.fruitjuice;

/**
 * The entry point to the FruitJuice framework.
 * <code>
 * FruitJuice.createInjector(new Context.Builder() {
 *   Map<InjectionRequest, Object> registry = new HashMap<>();
 *
 *   @Override public Context.Builder add(InjectionPoint injectionPoint) {
 *     registry.put(
 *       injectionPoint.getRequest(),
 *       injectionPoint.getValueFactory().create(injectionPoint.getRequest())
 *     );
 *     return this;
 *   }
 *
 *   @Override
 *   public Context build() {
 *     return new Context() {
 *       @Override
 *       public <V> V lookup(InjectionRequest request) {
 *         //noinspection unchecked
 *         return (V) registry.get(request);
 *       }
 *    };
 *   }
 * }).getInstance(checkNotNull(fixtureClass));
 * </code>
 */
public enum FruitJuice {
  ;

  public static Injector createInjector(Context.Builder builder) {
    return new Injector.Impl(builder);
  }
}
