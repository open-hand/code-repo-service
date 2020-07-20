package org.hrds.rducm.gitlab.infra.util;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by younger on 2018/3/29.
 */
public class TypeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeUtil.class);
    public static final String SEARCH_PARAM = "searchParam";
    public static final String PARAMS = "params";
    private static final Gson GSON = new Gson();

    private TypeUtil() {
    }


    /**
     * 对象转换
     *
     * @param obj obj
     * @param <T> t
     * @return t
     */
    public static <T> T cast(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return (T) obj;
        }
    }

    public static Map<String, Object> castMapParams(String params) {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put(TypeUtil.SEARCH_PARAM, null);
        mapParams.put(TypeUtil.PARAMS, null);

        if (!StringUtils.isEmpty(params)) {
            Map maps = GSON.fromJson(params, Map.class);
            mapParams.put(TypeUtil.SEARCH_PARAM, TypeUtil.cast(maps.get(TypeUtil.SEARCH_PARAM)));
            mapParams.put(TypeUtil.PARAMS, TypeUtil.cast(maps.get(TypeUtil.PARAMS)));
        }

        return mapParams;
    }

    public static String castToSearchParam(String key, String value) {
        Map<String, Map<String, Object>> mapParams = new HashMap<>(16);
        Map<String, Object> params = new HashMap<>(16);

        params.put(key, value);
        mapParams.put(TypeUtil.SEARCH_PARAM, params);

        return GSON.toJson(mapParams);
    }

    public static String castToSearchParam(Map<String, Object> params) {
        Map<String, Map<String, Object>> mapParams = new HashMap<>(16);

        mapParams.put(TypeUtil.SEARCH_PARAM, params);

        return GSON.toJson(mapParams);
    }

    /**
     * 判断对象中属性值是否全为空
     *
     * @param object
     * @return
     */
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }

        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null && StringUtils.isNotBlank(f.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.info("exception", e);
        }
        return true;
    }

    public static <T> List<T> getListWithType(Map<Class, List> map, Class<T> key) {
        return (List<T>) map.get(key);
    }

    public static class ParamsBuilder {
        private Map<String, Object> searchParamMap = new HashMap<>();
        private Map<String, Object> paramMap = new HashMap<>();

        public ParamsBuilder searchParam(String key, Object value) {
            searchParamMap.put(key, value);
            return this;
        }

        public ParamsBuilder param(String key, Object value) {
            paramMap.put(key, value);
            return this;
        }

        public String build() {
            Map<String, Object> result = new HashMap<>();
            if (!searchParamMap.isEmpty()) {
                result.put(TypeUtil.SEARCH_PARAM, searchParamMap);
            }

            if (!paramMap.isEmpty()) {
                result.put(TypeUtil.PARAMS, paramMap);
            }

            return GSON.toJson(result);
        }
    }
}
