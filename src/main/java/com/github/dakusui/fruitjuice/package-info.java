/**
 * "Fruit-juice" is a simple dependency injection library, like Guice.
 * <p>
 * Guice is a sophisticated and very feature rich library for broader purposes
 * but at the same time difficult to use some times.
 * <p>
 * In Guice, values to be injected are provided based on conditions satisfied by
 * "injection points" And those conditions are composed from type, generic type
 * parameters, type of annotations given to the target elements.
 * If multiple points satisfy the same set of conditions, the same provider
 * (or instance) will be injected to all those points.
 * And the condition (usually) cannot contain attributes of an annotation.
 *
 * Therefore, it is hard to wire objects in following code snippet by Guice.
 *
 * <pre>
 *    {@literal @}Inject
 *    {@literal @}Add({1,2,3})
 *    private int sum;  // I want to inject 6 (=1+2+3) to this field by Guice.
 * </pre>
 *
 * In order to achieve this, you can use a mechanism called "CustomInjections",
 * but it seemed to me a bit more complicated than it can be.
 *
 * Fruit-juice is a library specialized in this sort of use cases. Following is a
 * diagram that illustrates what happens behind the scene of the framework.
 *
 * <pre>
 * +----+                                                       +---------------+
 * |User|                                                       | Class&lt;Target&gt;|
 * +----+                                                       +---------------+
 *    |new        +---------------+                                     |
 *    |----------&gt;|Context.Builder|                                     |
 *    |           +---------------+                                     |
 *    |                   |                                             |
 *    |FruitJuice.createInjector(Context.Builder +--------+             |
 *    |-----------------------------------------&gt;|Injector|             |
 *    |                   |                      +--------+             |
 *    |getInstance(Class&lt;Target&gt;)                     |                 |
 *    |----------------------------------------------&gt;|                 |
 *    |                   |                           |collect injection points and
 *    |                   |                           |determine constructor
 *    |                   |                           |----------------&gt;||
 *    |                   |                           |                 ||
 *    |                   |add(InjectionPoint)        |                 |
 *    |                   ||&lt;-------------------------||                |
 *    |                   ||                          ||                |
 *    :                   :                           :                 :
 *    |                   |                           ||                |
 *    |                   |build()                    ||                |
 *    |                   ||&lt;-------------------------||                |
 *    |                   ||                          ||                |
 *    |                   ||      +-------+           ||                |
 *    |                   ||-----&gt;|Context|           ||                |
 *    |                   ||      +-------+           ||                |
 *    |                   ||          |               ||                |
 *    |                   |           |lookup(InjectionRequest)         |
 *    |                   |           ||&lt;-------------||                |
 *    |                   |           ||              ||                |
 *    :                   :           :               :                 :
 *    |                   |           |               ||                |
 *    |                   |           |               |invoke constructor with injected parameter values
 *    |                   |           |               ||---------------&gt;||
 *    |                   |           |               ||                ||new       +------+
 *    |                   |           |               ||                ||---------&gt;|Target|
 *    |                   |           |               ||                ||          +------+
 *    |                   |           |               ||                |               |
 *    |                   |           |               ||                |inject field values
 *    |                   |           |               ||                ||-------------&gt;||
 *    :                   :           :               :                 :               :
 *    |                   |           |               ||                |               |
 *    |                   |           |               ||
 *    |&lt; - - - - - - - - - - - - - - - - - - - - - - -||
 *    |                   |           |               |
 *    |                   |           |               |
 *
 * </pre>
 *
 * Names and semantics of classes included in Fruit-juice library are meant to be
 * similar to ones in Guice.
 *
 * A major difference between them is to give {@code Context.Builder} object to
 * {@code FruitJuice.createInjector(...)} method, instead of {@code Module}s.
 *
 * {@code Injector} adds injection points, which have accesses to annotations given to
 * themselves, to the builder on injection operation.
 *
 * Once all the injection points are added to the builder, it will then calls {@code build()}
 * method, which creates an actual {@code Context}.
 *
 * From this {@code Context} object, the {@code Injector} looks up an appropriate
 * object to be injected by {@code InjectionRequest}, which is held by {@code InjectionPoint},
 * for each point.
 *
 * To help a task to implement the above mentioned {@code Context.Builder}, an
 * abstract class {@code Context.Builder.Base} is provided.
 *
 * @see com.github.dakusui.fruitjuice.FruitJuice
 * @see com.github.dakusui.fruitjuice.Context
 * @see com.github.dakusui.fruitjuice.Context.Builder
 * @see com.github.dakusui.fruitjuice.Context.Builder.Base
 * @see com.github.dakusui.fruitjuice.Injector
 * @see com.github.dakusui.fruitjuice.InjectionPoint
 * @see com.github.dakusui.fruitjuice.InjectionRequest
 * @see <a href="https://github.com/google/guice/wiki/CustomInjections">Custom injections of Guice</a>
 */
package com.github.dakusui.fruitjuice;