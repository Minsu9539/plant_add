package com.example.plnatsub;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class PlantInfo extends Activity {
    private EditText etMessage;
    private Button btnSend;
    private TextView tvRecvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_info);

        etMessage = (EditText) findViewById(R.id.et_message);
        btnSend = (Button) findViewById(R.id.btn_sendData);
        tvRecvData = (TextView) findViewById(R.id.tv_recvData);

        /* Send 버튼을 눌렀을 때 서버에 데이터를 보내고 받는다 */
        btnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String sMessage = etMessage.getText().toString(); // 보내는 메시지를 받아옴
                String result = SendByHttp(sMessage); // 메시지를 서버에 보냄
                String[][] parsedData = jsonParserList(); // 받은 메시지를 json 파싱

                tvRecvData.setText(result); // 받은 메시지를 화면에 보여주기

            }

        });

    }




    /**
     * 서버에 데이터를 보내는 메소드
     * @param msg
     * @return
     */

    private String SendByHttp(String msg) {
        if(msg == null)
            msg = "";

// 서버를 설정해주세요!!!
        String URL = "http://127.0.0.1:8080/MyServer/JSONServer.jsp";
        DefaultHttpClient client = new DefaultHttpClient();
        try {

            /* 체크할 id와 pwd값 서버로 전송 */
            HttpPost post = new HttpPost(URL+"?msg="+msg);
            /* 지연시간 최대 3초 */
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);



            /* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));
            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown(); // 연결 지연 종료
            return "";

        }



    }



    /**

     * 받은 JSON 객체를 파싱하는 메소드

     * @param page

     * @return

     */

    public String[][] jsonParserList(String pRecvServerPage) {



        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);



        try {

            JSONObject json = new JSONObject(pRecvServerPage);

            JSONArray jArr = json.getJSONArray("List");





            // 받아온 pRecvServerPage를 분석하는 부분

            String[] jsonName = {"msg1", "msg2", "msg3"};

            String[][] parseredData = new String[jArr.length()][jsonName.length];

            for (int i = 0; i < jArr.length(); i++) {

                json = jArr.getJSONObject(i);

                if(json != null) {

                    for(int j = 0; j < jsonName.length; j++) {

                        parseredData[i][j] = json.getString(jsonName[j]);

                    }

                }

            }





            // 분해 된 데이터를 확인하기 위한 부분

            for(int i=0; i<parseredData.length; i++){

                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);

                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);

                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][2]);

            }



            return parseredData;

        } catch (JSONException e) {

            e.printStackTrace();

            return null;

        }

    }



}


