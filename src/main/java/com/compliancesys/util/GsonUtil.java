package com.compliancesys.util;

import java.io.BufferedReader;
import java.io.IOException;

public interface GsonUtil {

    <T> String serialize(T object);

    <T> T deserialize(String json, Class<T> type);

    <T> T deserialize(BufferedReader reader, Class<T> type) throws IOException;
}
