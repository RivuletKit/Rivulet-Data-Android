package com.tzmax.rivuletdata.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestCollection {

    public static enum Method {
        GET,
        POST,
    }

    // 属性
    Method method;
    ArrayList<Header> header;
    Url url;
    Body body;

    public RequestCollection jsonDecoding(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);

        // 取 method 参数
        String methodStr = jsonObj.getString("method");
        this.method = stringToMethod(methodStr);

        // 取 Url 对象
        JSONObject urlObj = jsonObj.getJSONObject("url");
        this.url = Url.Instance().jsonObjectDecoding(urlObj);

        // 取 header 数组
        if (!jsonObj.isNull("header")) {
            this.header = new ArrayList<>();
            JSONArray headerArray = jsonObj.getJSONArray("header");
            for (int i = 0; i < headerArray.length(); i++) {
                JSONObject itemObj = headerArray.getJSONObject(i);
                String itemKeyStr = jsonGetValue(itemObj, "key"),
                        itemValueStr = jsonGetValue(itemObj, "value"),
                        itemTypeStr = jsonGetValue(itemObj, "type");
                boolean itemDisabled = jsonObj.isNull("disabled") || jsonObj.getBoolean("disabled");
                this.header.add(new Header(itemKeyStr, itemValueStr, itemTypeStr, itemDisabled));
            }
        }

        // 取 Body 对象
        if (!jsonObj.isNull("body")) {
            JSONObject bodyObj = jsonObj.getJSONObject("body");
            this.body = Body.Instance().jsonObjectDecoding(bodyObj);
        }


        return this;
    }

    private String jsonGetValue(JSONObject obj, String name) throws JSONException {
        return obj.isNull(name) ? "" : obj.getString(name);
    }

    static class Header {
        String key, value, type;
        Boolean disabled;

        public Header(String key, String value, String type, Boolean disabled) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.disabled = disabled;
        }

        public Header(String key, String value, String type) {
            this(key, value, type, true);
        }

        public Header(String key, String value) {
            this(key, value, null);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }
    }

    static class Query {
        String key, value;
        Boolean disabled;

        public Query(String key, String value) {
            this(key, value, true);
        }

        public Query(String key, String value, Boolean disabled) {
            this.key = key;
            this.value = value;
            this.disabled = disabled;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }

    }

    static class Url {
        String raw, protocol, port;
        ArrayList<String> host, path;
        ArrayList<Query> querys;

        // 通过 JSONObject 解析出 Url 对象
        public Url jsonObjectDecoding(JSONObject jsonObj) throws JSONException {

            this.raw = jsonObj.isNull("raw") ? "" : jsonObj.getString("raw");
            this.protocol = jsonObj.isNull("protocol") ? "" : jsonObj.getString("protocol");
            this.port = jsonObj.isNull("port") ? "" : jsonObj.getString("port");

            this.host = new ArrayList<>();
            JSONArray hosts = jsonObj.getJSONArray("host");
            for (int i = 0; i < hosts.length(); i++) {
                String item = hosts.getString(i);
                this.host.add(item);
            }

            this.path = new ArrayList<>();
            JSONArray paths = jsonObj.getJSONArray("path");
            for (int i = 0; i < paths.length(); i++) {
                String item = hosts.getString(i);
                this.path.add(item);
            }

            this.querys = new ArrayList<>();
            JSONArray queryArray = jsonObj.getJSONArray("query");
            for (int i = 0; i < queryArray.length(); i++) {
                JSONObject item = queryArray.getJSONObject(i);

                String itemKeyStr = item.isNull("key") ? "" : item.getString("key");
                String itemValueStr = item.isNull("value") ? "" : item.getString("value");
                Boolean itemDisabled = item.isNull("disabled") || item.getBoolean("disabled");

                this.querys.add(new Query(itemKeyStr, itemValueStr, itemDisabled));
            }

            return this;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public ArrayList<String> getHost() {
            return host;
        }

        public void setHost(ArrayList<String> host) {
            this.host = host;
        }

        public ArrayList<String> getPath() {
            return path;
        }

        public void setPath(ArrayList<String> path) {
            this.path = path;
        }

        public ArrayList<Query> getQuerys() {
            return querys;
        }

        public void setQuerys(ArrayList<Query> querys) {
            this.querys = querys;
        }

        // 构建 Url 对象
        public static Url Instance() {
            return new Url();
        }
    }

    static class Body {

        public static enum BodyMode {
            FORMDATA,
            URLENCODED,
            RAW
        }

        BodyMode mode;
        String raw, rawLanguage;
        ArrayList<BodyNode> urlencoded;
        ArrayList<BodyNode> formdata;

        // 通过 JSONObject 解析出 Body 对象
        public Body jsonObjectDecoding(JSONObject jsonObj) throws JSONException {

            String modeStr = jsonObj.isNull("mode") ? "" : jsonObj.getString("mode");
            this.mode = stringToBodyMode(modeStr);

            this.raw = jsonObj.isNull("raw") ? "" : jsonObj.getString("raw");
            if (!jsonObj.isNull("options") &&
                    !jsonObj.getJSONObject("options").isNull("raw") &&
                    !jsonObj.getJSONObject("options").getJSONObject("raw").isNull("language")
            ) {
                this.rawLanguage = jsonObj.getJSONObject("options").getJSONObject("raw").getString("language");
            }

            this.urlencoded = new ArrayList<>();
            if (!jsonObj.isNull("urlencoded")) {
                JSONArray urlencodedArray = jsonObj.getJSONArray("urlencoded");
                for (int i = 0; i < urlencodedArray.length(); i++) {
                    JSONObject item = urlencodedArray.getJSONObject(i);
                    String itemKeyStr = item.isNull("key") ? "" : item.getString("key");
                    String itemValueStr = item.isNull("value") ? "" : item.getString("value");
                    String itemTypeStr = item.isNull("type") ? "" : item.getString("type");
                    this.urlencoded.add(new BodyNode(itemKeyStr, itemValueStr, itemTypeStr));
                }
            }

            this.formdata = new ArrayList<>();
            if (!jsonObj.isNull("formdata")) {
                JSONArray formdataArray = jsonObj.getJSONArray("formdata");
                for (int i = 0; i < formdataArray.length(); i++) {
                    JSONObject item = formdataArray.getJSONObject(i);
                    String itemKeyStr = item.isNull("key") ? "" : item.getString("key");
                    String itemValueStr = item.isNull("value") ? "" : item.getString("value");
                    String itemTypeStr = item.isNull("type") ? "" : item.getString("type");
                    this.formdata.add(new BodyNode(itemKeyStr, itemValueStr, itemTypeStr));
                }
            }


            return this;
        }

        public BodyMode getMode() {
            return mode;
        }

        public void setMode(BodyMode mode) {
            this.mode = mode;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public String getRawLanguage() {
            return rawLanguage;
        }

        public void setRawLanguage(String rawLanguage) {
            this.rawLanguage = rawLanguage;
        }

        public ArrayList<BodyNode> getUrlencoded() {
            return urlencoded;
        }

        public void setUrlencoded(ArrayList<BodyNode> urlencoded) {
            this.urlencoded = urlencoded;
        }

        public ArrayList<BodyNode> getFormdata() {
            return formdata;
        }

        public void setFormdata(ArrayList<BodyNode> formdata) {
            this.formdata = formdata;
        }

        // 构建 Url 对象
        public static Body Instance() {
            return new Body();
        }

        // 字符串转 Method 枚举
        public static BodyMode stringToBodyMode(String str) {
            switch (str) {
                case "formdata":
                    return BodyMode.FORMDATA;
                case "raw":
                    return BodyMode.RAW;
                case "urlencoded":
                    return BodyMode.URLENCODED;
                default:
                    return null;
            }
        }

    }

    static class BodyNode {
        String key, value, type;
        Boolean disabled;

        public BodyNode(String key, String value, String type, Boolean disabled) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.disabled = disabled;
        }

        public BodyNode(String key, String value, String type) {
            this(key, value, type, true);
        }

        public BodyNode(String key, String value) {
            this(key, value, null);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(Boolean disabled) {
            this.disabled = disabled;
        }
    }

    // 构建 RequestCollection 对象
    public static RequestCollection Instance() {
        return new RequestCollection();
    }

    // 字符串转 Method 枚举
    public static Method stringToMethod(String str) {
        switch (str) {
            case "get":
                return Method.GET;
            case "post":
                return Method.POST;
            default:
                return null;
        }
    }

}