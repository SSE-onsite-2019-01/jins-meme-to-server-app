package sse;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.ByteBuffer;

public class JinsMemeSubscriber implements MqttCallback {

    public JinsMemeSubscriber()
    {

    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println(throwable.getLocalizedMessage());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        double stress = ByteBuffer.wrap(mqttMessage.getPayload()).getDouble();
        System.out.println(stress);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
