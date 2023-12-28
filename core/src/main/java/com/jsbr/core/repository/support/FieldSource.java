package com.jsbr.core.repository.support;

public record FieldSource(String name, Object value) {

    public static FieldSource empty() {
        return new FieldSource(null, null);
    }

    public boolean isEmpty() {
        return this.name == null && this.value == null;
    }

}
