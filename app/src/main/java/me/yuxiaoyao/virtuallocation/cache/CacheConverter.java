package me.yuxiaoyao.virtuallocation.cache;

public interface CacheConverter<T> {

    <S> T serialize(S source);

    <C> C deserialize(T target, Class<C> cls);

}
