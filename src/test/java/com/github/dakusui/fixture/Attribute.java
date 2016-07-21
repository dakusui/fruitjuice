package com.github.dakusui.fixture;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public interface Attribute {
  <T> T apply();

  class Basic implements Attribute {
    private final Fixture.Value value;

    public Basic(Fixture.Value value) {
      this.value = checkNotNull(value);
    }

    @Override
    public <T> T apply() {
      return (T) value.value();
    }
  }

  interface Bundle {
    Attribute config(String name);

    String type();

    class Basic implements Bundle {
      private final Fixture.Value[] configs;
      private final Fixture.Value[] dependencies;
      private final Fixture.Value[] childComponents;
      private final String          type;

      public Basic(String type, Fixture.Value[] configs, Fixture.Value[] dependencies, Fixture.Value[] childComponents) {
        this.type = type;
        this.configs = configs;
        this.dependencies = dependencies;
        this.childComponents = childComponents;
      }

      @Override
      public Attribute config(String name) {
        Preconditions.checkNotNull(name);
        if (this.configs == null) {
          throw new UnsupportedOperationException(format("'%s' does not support this operation.", this.toString()));
        }
        for (Fixture.Value each : this.configs) {
          if (name.equals(each.name()))
            return new Attribute.Basic(each);
        }
        throw new IllegalArgumentException(format("Attribute '%s' was not found in '%s'", name, this.toString()));
      }

      @Override
      public String type() {
        return this.type;
      }
    }
  }
}
