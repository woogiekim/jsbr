package com.jsbr.core.repository.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldSources {

    private final List<FieldSource> sources;

    private FieldSources(List<FieldSource> sources) {
        this.sources = sources;
    }

    public static FieldSources newInstance() {
        return new FieldSources(new ArrayList<>());
    }

    public FieldSources add(FieldSource source) {
        if (!this.sources.contains(source)) {
            this.sources.add(source);
        }

        return this;
    }

    public void remove(String name) {
        this.sources.removeIf(source -> source.name().equals(name));
    }

    public Map<String, Object> toMap() {
        var map = new HashMap<String, Object>();

        for (var source : this.sources) {
            map.put(source.name(), source.value());
        }

        return map;
    }

    public Set<String> toNameSet() {
        return this.toMap().keySet();
    }

}
