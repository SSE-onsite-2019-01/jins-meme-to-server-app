package sse;

import android.os.AsyncTask;

import com.jins_jp.meme.MemeRealtimeData;
import com.jins_meme.visualizing_blinks_and_6axis.ConnectActivity;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;


/**
 * Created by doi on 2018/03/07.
 */
public class JinsMemePublisher extends AsyncTask<MemeDoubleData, Void, Boolean> {

//    private static String hostName = "10.7.1.35";
//    private static int port_no = 5550;

    private final static Logger logger = LoggerFactory.getLogger(JinsMemePublisher.class);

    private String broker;
    private String clientId;

    private MQTT mqttCallback;

    // brokerのURLは/app/src/main/res/values/string.xmlの中で設定
    public JinsMemePublisher(String broker, String clientId) {
        this.broker = broker;
        this.clientId = clientId;

        mqttCallback = new MQTT();
    }



    public void publish(MemeDoubleData data) {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setKeepAliveInterval(60);

        MqttClient myClient;

        // ブローカーに接続
        try {
            //androidClient = new MqttAndroidClient(activity, this.broker, this.clientId);
            //androidClient.connect(options);

            myClient = new MqttClient(this.broker, this.clientId, new MemoryPersistence());
            myClient.setCallback(mqttCallback);
            //myClient.connect();
            myClient.connect(options);
        } catch (MqttException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return;
        }

        try
        {
            this.publishMessage(myClient, options, "EyeMoveUp", data.getAverageEyeMoveUp());
            this.publishMessage(myClient, options, "EyeMoveDown", data.getAverageEyeMoveDown());
            this.publishMessage(myClient, options, "EyeMoveLeft", data.getAverageEyeMoveLeft());
            this.publishMessage(myClient, options, "EyeMoveRight", data.getAverageEyeMoveRight());
            this.publishMessage(myClient, options, "BlinkSpeed", data.getAverageBlinkSpeed());
            this.publishMessage(myClient, options, "BlinkStrength", data.getAverageBlinkStrength());
            this.publishMessage(myClient, options, "Walking", data.getAverageWalking());
            this.publishMessage(myClient, options, "Roll", data.getAverageRoll());
            this.publishMessage(myClient, options, "Pitch", data.getAveragePitch());
            this.publishMessage(myClient, options, "Yaw", data.getAverageYaw());
            this.publishMessage(myClient, options, "AccX", data.getAverageAccX());
            this.publishMessage(myClient, options, "AccY", data.getAverageAccY());
            this.publishMessage(myClient, options, "AccZ", data.getAverageAccZ());

        }catch (MqttException e)
        {
            e.printStackTrace();
            try {
                //androidClient.disconnect();
                myClient.disconnect();
            } catch (MqttException e1) {
                e1.printStackTrace();
            }
        }

        // ブローカーから切断
        try {
            //androidClient.disconnect();
            myClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    protected Boolean doInBackground(MemeDoubleData... memeDoubleDatas) {
        publish(memeDoubleDatas[0]);
        return true;
    }

    private byte[] convertToByteArray(double value){
        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putDouble(value);
        return buffer.array();
    }

    private void publishMessage(MqttClient client, MqttConnectOptions option, String name, double value) throws MqttException {
        MqttMessage message = new MqttMessage(convertToByteArray(value));
        message.setQos(0);
        message.setRetained(false);

        client.publish("jins-meme/" + name, message);
    }

    private void publishMessage(MqttAndroidClient client, MqttConnectOptions option, String name, double value) throws MqttException
    {
        MqttMessage message = new MqttMessage((convertToByteArray(value)));
        message.setQos(0);
        message.setRetained(false);

        client.publish("jins-meme/" + name, message);
    }
}
