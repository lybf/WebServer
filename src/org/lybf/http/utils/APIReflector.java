package org.lybf.http.utils;

import org.lybf.http.annation.Ignore;
import org.lybf.http.api.BaseApi;
import org.lybf.http.beans.RawHttpURL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

public class APIReflector {
    public static void setParamters(RawHttpURL rawHttpURL, Object target) throws Exception {
        HashMap<String, String> keys = rawHttpURL.getKeys();
        Class c = target.getClass();

        if(keys.keySet() == null || keys.keySet().size() <= 0)return;
        label:
        for (String key : keys.keySet()) {
            try {
                Field field = c.getDeclaredField(key);
                field.setAccessible(true);
                for (Annotation annotation :
                        field.getAnnotations()) {
                    if (annotation instanceof Ignore) {
                        continue label;
                    }
                }
                field.set(target, keys.get(key));
            } catch (NoSuchFieldException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw e;
            }
        }
    }

}
