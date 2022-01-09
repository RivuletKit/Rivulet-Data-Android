package com.tzmax.template;

import android.os.Bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.tzmax.rivuletdata.request.RequestCollection;
import com.tzmax.rivuletdata.request.RequestUtils;
import com.tzmax.template.databinding.ActivityExampleJqBinding;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExampleJQ extends AppCompatActivity {

    private ActivityExampleJqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExampleJqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

    }

    private void initView() {

        // 初始化请求数据
        initCollectionValue();

        binding.eBtnCallrun.setOnClickListener(v -> {
            String collectionStr = binding.eEditCollection.getText().toString();
            if (collectionStr.equals("")) {
                toast("请求规则不得定义为空。");
                return;
            }

            // TODO: 开始实现 collectionStr to okhttp
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Request request = RequestUtils.jsonToRequest(collectionStr);
                    if (request != null) {
                        OkHttpClient client = new OkHttpClient.Builder()
                                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String data = response.body().string();
                            loadCallBackView(data);

//                            JsonQuery jq = JsonQuery.compile(".answer");
//                            JsonNode in = new ObjectMapper().readTree(data);
//                            List<JsonNode> nodes = jq.apply(Scope.newEmptyScope(), in);
//                            JsonNode node = nodes.get(0);
//                            Log.d(TAG, "testJq: " + node.textValue());

                        } catch (IOException e) {
                            e.printStackTrace();
                            loadCallBackView(e.getMessage());
                        }
                    }
                }
            }).start();


        });
    }

    private void toast(String s) {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCallBackView(String call) {
        Log.d("zmide", "loadCallBackView: " + call);
        runOnUiThread(() -> {
            binding.eEditCallback.setText(call);
        });
    }

    private void initCollectionValue() {
        binding.eEditCollection.setText("{\n" +
                "            \"method\":\"GET\",\n" +
                "                    \"header\": [],\n" +
                "            \"url\":{\n" +
                "                \"raw\":\"https://study.jszkk.com/api/open/seek?q=数据\",\n" +
                "                        \"protocol\":\"https\",\n" +
                "                        \"host\": [\n" +
                "                \"study\",\n" +
                "                        \"jszkk\",\n" +
                "                        \"com\"\n" +
                "\t\t\t\t\t],\n" +
                "                \"path\": [\n" +
                "                \"api\",\n" +
                "                        \"open\",\n" +
                "                        \"seek\"\n" +
                "\t\t\t\t\t],\n" +
                "                \"query\": [\n" +
                "                {\n" +
                "                    \"key\":\"q\",\n" +
                "                        \"value\":\"数据\"\n" +
                "                }\n" +
                "\t\t\t\t\t]\n" +
                "            }\n" +
                "        }");
    }

}