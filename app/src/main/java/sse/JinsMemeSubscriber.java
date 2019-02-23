package sse;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.android.gms.cast.framework.SessionManager;

import java.nio.ByteBuffer;

public class JinsMemeSubscriber implements MqttCallback {

    private SessionManager manager;

    private double lastScore = 0;

    public JinsMemeSubscriber(SessionManager manager) {
        this.manager = manager;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println(throwable.getLocalizedMessage());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        double stress = ByteBuffer.wrap(mqttMessage.getPayload()).getDouble();
        lastScore = stress;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public double getLastScore() {
        return lastScore;
    }
}
