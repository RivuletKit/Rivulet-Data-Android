package com.tzmax.rivuletdata.request;

import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONException;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestUtils {

    public static Request jsonToRequest(String jsonStr) {
        RequestCollection apiReqCol = RequestCollection.Instance();
        try {
            apiReqCol.jsonDecoding(jsonStr);

            Request.Builder requestBuilder = new Request.Builder().url(apiReqCol.url.raw);

            if (apiReqCol.header != null && apiReqCol.header.size() > 0) {
                Map<String, String> headerMap = new ArrayMap<>();
                for (RequestCollection.Header item : apiReqCol.header) {
                    headerMap.put(item.key, item.value);
                }
                requestBuilder.headers(Headers.of(headerMap));
            }


            if (apiReqCol.method == RequestCollection.Method.GET) {
                requestBuilder.method("GET", null);
            } else if (apiReqCol.method == RequestCollection.Method.POST) {

                RequestBody body = null;
                if (apiReqCol.body != null) {
                    if (apiReqCol.body.mode == RequestCollection.Body.BodyMode.FORMDATA) {
                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                        if (apiReqCol.body.formdata != null) {
                            for (RequestCollection.BodyNode item : apiReqCol.body.formdata) {
                                if (item.disabled) {
                                    builder.addFormDataPart(item.key, item.value);
                                }
                            }
                        }
                        body = builder.build();
                    }
                }
                requestBuilder.method("POST", body);

            } else {
                requestBuilder.method("GET", null);
            }

            return requestBuilder.build();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
