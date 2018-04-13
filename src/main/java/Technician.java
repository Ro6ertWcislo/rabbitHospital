import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Technician {
    private final UUID uuid;
    private final RabbitConnection connection;
    private final Channel channel;

    public Technician() throws IOException, TimeoutException {
        this.uuid = UUID.randomUUID();;
        this.connection = new RabbitConnection();
        this.channel = connection.getChannel();
    }

    public void run() throws Exception {

        System.out.println("Technician with id " + uuid + " started.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter first Specialization: ");
        String firstSpec = br.readLine();
        System.out.println("Enter second Specialization: ");
        String secondSpec = br.readLine();



        channel.exchangeDeclare(Config.EXAMINATION_EXCHANGE, BuiltinExchangeType.DIRECT);


        channel.queueDeclare(firstSpec, false, false, false, null);
        channel.queueBind(firstSpec, Config.EXAMINATION_EXCHANGE, firstSpec);
        channel.queueDeclare(secondSpec, false, false, false, null);
        channel.queueBind(secondSpec, Config.EXAMINATION_EXCHANGE, secondSpec);



        channel.exchangeDeclare(Config.DOCTOR_REPLY, BuiltinExchangeType.DIRECT);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                System.out.println("sending to "+properties.getReplyTo());
                channel.basicPublish(Config.DOCTOR_REPLY, properties.getReplyTo(), null, "dupeczka".getBytes("UTF-8"));

            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(firstSpec, true, consumer);
        channel.basicConsume(secondSpec, true, consumer);


        //////////////////// producer /////////////////







    }


}
