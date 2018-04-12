import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Technician {

    public static void main(String[] argv) throws Exception {

        UUID uuid = UUID.randomUUID();
        System.out.println("Technician with id " + uuid + " started.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter first Specialization: ");
        String firstSpec = br.readLine();
        System.out.println("Enter second Specialization: ");
        String secondSpec = br.readLine();


        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = Config.EXAMINATION_EXCHANGE;
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // queue & bind
//        String queueName = channel.queueDeclare().getQueue();
//        channel.queueBind(queueName, EXCHANGE_NAME, "");
//        System.out.println("created queue: " + queueName);

        // consumer (message handling)
        channel.queueDeclare(firstSpec, false, false, false, null);
        channel.queueBind(firstSpec, EXCHANGE_NAME, firstSpec);
        channel.queueDeclare(secondSpec, false, false, false, null);
        channel.queueBind(secondSpec, EXCHANGE_NAME, secondSpec);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                System.out.println(properties.getReplyTo());
            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(firstSpec, true, consumer);
        channel.basicConsume(secondSpec, true, consumer);
    }

}
