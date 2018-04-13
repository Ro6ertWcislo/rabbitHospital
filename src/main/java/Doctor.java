import com.rabbitmq.client.*;

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


        System.out.println("Doctor with id "+uuid+" started.");


        channel.exchangeDeclare(Config.EXAMINATION_EXCHANGE, BuiltinExchangeType.DIRECT);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        /////////////// consumer ///////////////////////////////


        RabbitConsumer rabbitConsumer = new RabbitConsumer(channel,
                Config.DOCTOR_REPLY,
                Arrays.asList(Config.REPLY_QUEUE),
                Arrays.asList(uuid.toString()),
                BuiltinExchangeType.DIRECT);
//        channel.exchangeDeclare(Config.DOCTOR_REPLY,BuiltinExchangeType.DIRECT);
//
//
//        channel.queueDeclare(Config.REPLY_QUEUE, false, false, false, null);
//        channel.queueBind(Config.REPLY_QUEUE, Config.DOCTOR_REPLY, uuid.toString());
//
//
//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                String message = new String(body, "UTF-8");
//                System.out.println("Received: " + message);
//
//            }
//        };
//
//        // start listening
//        channel.basicConsume(Config.REPLY_QUEUE, true, consumer);


















        ////////////////////////////////////////////////

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
