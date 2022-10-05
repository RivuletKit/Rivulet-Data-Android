package com.tzmax.rivuletdata.analyze;

import org.json.JSONException;
import org.json.JSONObject;

public class ContentNode {

    public enum Type {
        JSON, // json 数据格式
        HTML, // html 数据格式
        XML, // xml 数据格式
        SPA, // 需要 spa 渲染的 html 数据格式
    }

    private Type type;
    private String analyze, // 使用的解析程序
            rule; // 解析规则

    public ContentNode(Type type, String analyze, String rule) {
        this.type = type;
        this.analyze = analyze;
        this.rule = rule;
    }

    public ContentNode(JSONObject jsonObj) throws JSONException {
        if (jsonObj.isNull("type") || jsonObj.isNull("rule")) {
            throw new JSONException("ContentNode 缺失 type 或 rule 字段。");
        }

        // 开始处理数据类型
        switch (jsonObj.getString("type")) {
            case "JSON":
            case "json":
                this.type = Type.JSON;
                break;
            case "HTMl":
            case "html":
                this.type = Type.HTML;
                break;
            case "XML":
            case "xml":
                this.type = Type.XML;
                break;
            case "SPA":
            case "spa":
                this.type = Type.SPA;
                break;
            default:
                this.type = null;
                throw new JSONException("ContentNode 不支持的数据类型。");
        }
        if (this.type == Type.JSON) {
            this.analyze = "jq"; // 赋值 json 数据默认使用 jq 处理
        } else {
            this.analyze = "xpath"; // 其他数据格式默认使用 xpath 处理
        }

        // 开始处理 analyze
        if (!jsonObj.isNull("analyze")) {
            this.analyze = jsonObj.getString("analyze");
        }

        // 开始处理 rule
        this.rule = jsonObj.getString("rule");
    }

    // 使用 json 字符串构造 ContentNode 对象
    public ContentNode(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public Type getType() {
        return type;
    }

    public String getAnalyze() {
        return analyze;
    }

    public String getRule() {
        return rule;
    }

    // ContentNode 转 json string
    public String toJSONString() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", this.type.toString());
        jsonObj.put("analyze", this.analyze);
        jsonObj.put("rule", this.rule);
        return jsonObj.toString();
    }

}
