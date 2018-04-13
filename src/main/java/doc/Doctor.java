package doc;

import com.rabbitmq.client.*;
import util.Config;
import util.RabbitConnection;
import util.RabbitConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Doctor {
    private final RabbitConnection connection;
    private final Channel channel;
    private final UUID uuid;
    private final AMQP.BasicProperties properties;

    public Doctor() throws IOException, TimeoutException {
        this.connection = new RabbitConnection();
        this.channel = connection.getChannel();
        this.uuid = UUID.randomUUID();
        properties = new  AMQP.BasicProperties
                .Builder()
                .replyTo(uuid.toString())
                .build();
    }
    public  void run() throws Exception {
        System.out.println("doc.Doctor with id "+uuid+" started.");
        channel.exchangeDeclare(Config.EXAMINATION_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Config.LOG_EXCHANGE, BuiltinExchangeType.FANOUT);

        new RabbitConsumer(channel,
                Config.DOCTOR_REPLY_EXCHANGE,
                Arrays.asList(Config.REPLY_QUEUE),
                Arrays.asList(uuid.toString()),
                BuiltinExchangeType.TOPIC)
                .init();


        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
            channel.basicPublish(Config.LOG_EXCHANGE, key, properties, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }





    }
}
