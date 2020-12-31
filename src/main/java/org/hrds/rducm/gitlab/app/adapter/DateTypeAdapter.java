package org.hrds.rducm.gitlab.app.adapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.hzero.core.base.BaseConstants;

/**
 * gson 处理时间戳转日期类型
 *
 * @author 14589 2020/12/31 10:08
 */
public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private final DateFormat format = new SimpleDateFormat(BaseConstants.Pattern.DATETIME);

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Date(json.getAsJsonPrimitive().getAsLong());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        String dfString = format.format(src);
        return new JsonPrimitive(dfString);
    }
}
