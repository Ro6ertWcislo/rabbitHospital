import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Doctor {
    public static void main(String[] argv) throws Exception {

        UUID uuid = UUID.randomUUID();
        System.out.println("Doctor with id "+uuid+" started.");


        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        channel.exchangeDeclare(Config.EXAMINATION_EXCHANGE, BuiltinExchangeType.DIRECT);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        AMQP.BasicProperties properties = new  AMQP.BasicProperties
                .Builder()
                .replyTo(uuid.toString())
                .build();

        while (true) {

            // read msg
            System.out.println("Enter key: ");
            String key = br.readLine();
            System.out.println("Enter message: ");
            String message = br.readLine();

            // break condition
            if ("exit".equals(message)) {
                break;
            }

            // publish
            channel.basicPublish(Config.EXAMINATION_EXCHANGE, key, properties, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }
    }
}
