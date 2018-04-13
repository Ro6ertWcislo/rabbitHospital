package tech;

import com.rabbitmq.client.*;
import Config.Config;
import util.RabbitConnection;
import util.RabbitConsumer;

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
        channel.exchangeDeclare(Config.DOCTOR_REPLY_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Config.LOG_EXCHANGE, BuiltinExchangeType.FANOUT);
        this.consumer = getTechConsumer();
    }

    public void run() throws Exception {

        System.out.println("tech.Technician started.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter first Specialization: ");
        String firstSpec = br.readLine();
        System.out.println("Enter second Specialization: ");
        String secondSpec = br.readLine();


        new RabbitConsumer(channel,
                Config.EXAMINATION_EXCHANGE,
                Arrays.asList(firstSpec, secondSpec),
                Arrays.asList(firstSpec, secondSpec),
                BuiltinExchangeType.TOPIC,
                consumer)
                .init();

        new RabbitConsumer(channel,
                Config.INFO_EXCHANGE,
                null,
                null,
                BuiltinExchangeType.FANOUT)
                .init();


        System.out.println("Waiting for messages...");


    }


    public Consumer getTechConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                if (!envelope.getRoutingKey().isEmpty()) {
                    String reply = envelope.getRoutingKey() + " " + message + " " + "done.";
                    channel.basicPublish(Config.DOCTOR_REPLY_EXCHANGE, properties.getReplyTo(), null, reply.getBytes("UTF-8"));
                    channel.basicPublish(Config.LOG_EXCHANGE, properties.getReplyTo(), null, reply.getBytes("UTF-8"));
                }
            }
        };
    }
}
