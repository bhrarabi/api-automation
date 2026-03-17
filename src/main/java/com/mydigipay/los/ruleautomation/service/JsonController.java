package com.mydigipay.los.ruleautomation.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
/*
 * Author: f.bahramnejad
 */
public class JsonController {

    public static <T> T jsonToPojo(String json, Class<T> type) {
        try {
            return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> jsonToPojoList(String jsonString, Class<T> type) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CollectionType listType =
                    objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(jsonString, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T jsonFileToPojoUsingKey(String jsonPath, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File(jsonPath));
        String jsonString = jsonNode.get(key).toString();
        String part = jsonString.replace("\"" + key + "\":", "");
        return jsonToPojo(part, type);
    }

    public static <T> List<T> jsonFileToPojoListUsingKey(String jsonPath, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File(jsonPath));
        String jsonString = jsonNode.get(key).toString();
        String part = jsonString.replace("\"" + key + "\":", "");
        return jsonToPojoList(part, type);
    }

    public static <T> T jsonStringToPojoUsingKey(String json, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        String jsonString = jsonNode.get(key).toString();
        String part = jsonString.replace("\"" + key + "\":", "");
        return jsonToPojo(part, type);
    }

    public static <T> List<T> jsonStringToPojoListUsingKey(String json, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        String jsonString = jsonNode.get(key).toString();
        String part = jsonString.replace("\"" + key + "\":", "");
        return jsonToPojoList(part, type);
    }

    private static String convertConfigFileToJson() throws IOException {
        InputStream yaml = new FileInputStream("src/main/resources/application.yml");
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);
        yaml.close();
        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);

    }

    public static <T> T propertyToPojo(String key, Class<T> type) throws IOException {
        return jsonStringToPojoUsingKey(convertConfigFileToJson(), key, type);
    }

    public static <T> List<T> propertyToPojoList(String key, Class<T> type) throws IOException {
        return jsonStringToPojoListUsingKey(convertConfigFileToJson(), key, type);
    }

    public static String searchInJsonList(String jsonList, String searchKey, String searchValue) {
        JSONArray jsonArray = new JSONArray(jsonList);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has(searchKey) && jsonObject.getString(searchKey).equals(searchValue)) {
                return jsonObject.toString();

            }
        }
        return null;
    }

}
