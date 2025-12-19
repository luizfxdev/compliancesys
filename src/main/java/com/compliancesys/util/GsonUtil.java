package com.compliancesys.util;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

public interface GsonUtil {
    <T> String serialize(T src);
    <T> T deserialize(String json, Class<T> classOfT);
    <T> T deserialize(String json, Type typeOfT);
    <T> T deserialize(Reader reader, Class<T> classOfT) throws IOException;
    <T> T deserialize(Reader reader, Type typeOfT) throws IOException;
}