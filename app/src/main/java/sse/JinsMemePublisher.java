package sse;

import android.os.AsyncTask;

import com.jins_jp.meme.MemeRealtimeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by doi on 2018/03/07.
 */
public class JinsMemePublisher extends AsyncTask<MemeDoubleData, Void, Boolean> {

//    private static String hostName = "10.7.1.35";
//    private static int port_no = 5550;

    private final static Logger logger = LoggerFactory.getLogger(JinsMemePublisher.class);

    private String broker;
    private String clientId;

    // brokerのURLは/app/src/main/res/values/string.xmlの中で設定
    public JinsMemePublisher(String broker, String clientId) {
        this.broker = broker;
        this.clientId = clientId;
    }



    public void publish(MemeDoubleData data) {
      // サーバにデータを送信
    }



    @Override
    protected Boolean doInBackground(MemeDoubleData... memeDoubleDatas) {
        publish(memeDoubleDatas[0]);
        return true;
    }
}
