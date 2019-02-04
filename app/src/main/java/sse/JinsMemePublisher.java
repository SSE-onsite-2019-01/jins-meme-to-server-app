package sse;

import android.os.AsyncTask;

import com.jins_jp.meme.MemeRealtimeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


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
        OutputStream outputStream = null;
        try {
            //InetAddress address = InetAddress.getByName(this.broker);
            Socket socket = new Socket(this.broker, 80);
            outputStream = socket.getOutputStream();
            outputStream.write(data.getServerSendData());
            Thread.sleep(100);
        } catch (Exception e) {
            logger.error("Cannot connect to " + this.broker, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("Cannot close "+ this.broker, e);
                }
            }
        }
    }



    @Override
    protected Boolean doInBackground(MemeDoubleData... memeDoubleDatas) {
        publish(memeDoubleDatas[0]);
        return true;
    }
}
