import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Technician {
    private final RabbitConnection connection;
    private final Channel channel;
    private final Consumer consumer;

    public Technician() throws IOException, TimeoutException {
        this.connection = new RabbitConnection();
        this.channel = connection.getChannel();
        channel.exchangeDeclare(Config.DOCTOR_REPLY, BuiltinExchangeType.DIRECT);
        this.consumer =new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                channel.basicPublish(Config.DOCTOR_REPLY, properties.getReplyTo(), null, "dupeczka".getBytes("UTF-8"));
            }
        };
    }

    public void run() throws Exception {

        System.out.println("Technician started.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter first Specialization: ");
        String firstSpec = br.readLine();
        System.out.println("Enter second Specialization: ");
        String secondSpec = br.readLine();


        new RabbitConsumer(channel,
                Config.EXAMINATION_EXCHANGE,
                Arrays.asList(firstSpec, secondSpec),
                Arrays.asList(firstSpec, secondSpec),
                BuiltinExchangeType.DIRECT,
                consumer)
                .init();


        System.out.println("Waiting for messages...");



    }


}
