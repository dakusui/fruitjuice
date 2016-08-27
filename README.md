# Fruit-juice
A java dependency injection library simpler than Guice.

[![Build Status](https://travis-ci.org/dakusui/fruitjuice.svg?branch=master)](https://travis-ci.org/dakusui/fruitjuice)
[![codecov.io](https://codecov.io/github/dakusui/fruitjuice/coverage.svg?branch=master)](https://codecov.io/github/dakusui/fruitjuice?branch=master)

# Installation
Fruit-juice requires Java SE7 or later.
Following is a maven coordinate for Fruit-juice.

```xml

    <dependency>
      <groupId>com.github.dakusui</groupId>
      <artifactId>fruitjuice</artifactId>
      <version>[1.0.2,)</version>
    </dependency>
```

# Usage
## Motivation
In stack overflow, one question was asked for a while ago. 
"Does anyone out there know how I can access the value of the 'poolSize' parameter from a Guice Provider?"[[1]]

```java

    public class ContainsMultiThreadedExecutorService {
        private final ExecutorService executorService;
    
        @Inject
        public ContainsMultiThreadedExecutorService(@MultiThreaded(poolSize = 5) ExecutorService executorService) {
            this.executorService = executorService;
        }
    
    }
```

To me it looked a quite frequent use case, but the answers are like "You should use CustomInjection"[[2]],
"You can mimic how @Named does similar thing", or "You can't. That's not how binding annotations are intended to be used."

Maybe this is not a right way to use 'dependency injection', but still I want to 
do it.
Suppose that this is not a thread pool, which we don't need to try different implementations, 

and instead we want to inject RSA key implementation.
Doesn't this still make sense to do?

```java

    public class KeyHolder {
      @Inject 
      @Key(algorithm = Algorithm.RSA, keyLength = 2048) 
      PublicKey publicKey;
      
    }
```

I think we probably want to try several security provider implementations without 
modifying code.

Anyway, the Fruit-juice's motivation is to help writing codes presented in this 
section as easy as possible. (Maybe it's violating some 'dependency injection''s idea, 
though).

## Examples

The solution to the question mentioned in the previous section would be like following.

```java

    public class ContainsMultiThreadedExecutorService {
      private final ExecutorService executorService;
    
      @Inject
      public ContainsMultiThreadedExecutorService(@MultiThreaded(poolSize = 4) ExecutorService executorService) {
        this.executorService = executorService;
      }
    
      public static void main(String... args) {
        ContainsMultiThreadedExecutorService injected = FruitJuice.createInjector(new Context.Builder.Base() {
          @Override
          protected Object create(InjectionRequest request) {
            return Executors.newFixedThreadPool(request.getAnnotation(MultiThreaded.class).poolSize());
          }
        }).getInstance(ContainsMultiThreadedExecutorService.class);
    ...

```

Please refer to API reference[[0]], which explains external specification and 
design of the Fruit-juice framework.

# References
* [0] "API reference"
* [1] "custom Guice binding annotations with parameters"
* [2] "Custom Injections"
* [3] "Google Guice"

[0]: https://dakusui.github.io/fruitjuice/
[1]: http://stackoverflow.com/questions/5704918/custom-guice-binding-annotations-with-parameters
[2]: https://github.com/google/guice/wiki/CustomInjections
[3]: https://github.com/google/guice
