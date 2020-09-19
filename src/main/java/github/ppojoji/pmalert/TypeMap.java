package github.ppojoji.pmalert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TypeMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 4240257637229945922L;
    
    public TypeMap() {}
    public TypeMap(int capacity) {
        super(capacity);
    }
    public TypeMap(Map<? extends String, ? extends Object> src) {
        super(src);
    }
    public TypeMap(int capacity, float loadFactor) {
        super(capacity, loadFactor);
    }
    public Integer getInt(String key) {
        return get(Integer.class, key);
    }
    public Integer asInt(String key) {
        Object v = this.get(key);
        if (v instanceof Integer) {
            return (Integer) v;
        } else {
            return Integer.parseInt(v.toString());
        }
    }
    public Integer asInt(String key, Integer defaultValue) {
        Object v = this.get(key);
        if (v instanceof Integer) {
            return (Integer) v;
        } else {
            try {
                return Integer.parseInt(v.toString());                
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }
    public Long getLong(String key) {
        return get(Long.class, key);
    }
    public Long asLong(String key) {
        Object v = this.get(key);
        if(v instanceof Long) {
            return (Long)v;
        } else {
            return Long.parseLong(v.toString());
        }
    }
    public Double getDouble(String key) {
        return get(Double.class, key);
    }
    public Double asDouble(String key) {
        Object v = this.get(key);
        if(v instanceof Double) {
            return (Double) v;
        } else {
            return Double.parseDouble(v.toString());
        }
    }
    public String getStr(String key) {
        return get(String.class, key);
    }
    public String asStr(String key) {
        Object v = this.get(key);
        return v == null ? null : v.toString();
    }
    private <T> T get(Class<T> cls, String key) {
        Object v = super.get(key);
        try {
            return cls.cast(v);
        } catch (ClassCastException e) {
            return cls.cast(exception("not a type of %s: %s(real type: %s)",
                    cls.getName(),
                    v.toString(),
                    v.getClass().getName()));            
        }
    }
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object v = super.get(key);
        try {
            return (T) v;
        } catch (ClassCastException e) {
            return (T) exception("not a expected type: %s(real type: %s)",
                    v.toString(),
                    v.getClass().getName());            
        }
    }
    private Object exception(String format, Object ... args) {
        throw new RuntimeException(String.format(format, args));
    }
    public static TypeMap with(String key, String value) {
        TypeMap m = new TypeMap(1);
        m.put(key, value);
        return m;
    }
    public <T, U> U validate(String key, Function<T, U> validator) {
        T value = get(key);
        return validator.apply(value);
    }
}
