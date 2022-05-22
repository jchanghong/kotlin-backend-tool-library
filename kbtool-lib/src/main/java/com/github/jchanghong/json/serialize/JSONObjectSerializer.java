package com.github.jchanghong.json.serialize;

import com.github.jchanghong.json.JSONObject;

/**
 * 对象的序列化接口，用于将特定对象序列化为{@link JSONObject}
 * @param <V> 对象类型
 * 
 * @author Looly
 */
@FunctionalInterface
public interface JSONObjectSerializer<V> extends JSONSerializer<JSONObject, V> {}
