package ott.hunter.database.homeContent.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import ott.hunter.models.home_content.LatestGoldType;

public class GoldConverter {
    @TypeConverter
    public static String fromList(List<LatestGoldType> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<LatestGoldType> jsonToList(String value){
        Type listType = new TypeToken<List<LatestGoldType>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
