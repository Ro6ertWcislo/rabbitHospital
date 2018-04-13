package util;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;

public class RabbitConsumer {
    private final Channel channel;
    private final Consumer consumer;
    private final String exchange;
    private final BuiltinExchangeType type;
    private final List<String> queues;
    private final List<String> keys;

    public RabbitConsumer(Channel channel, String exchange, List<String> queues, List<String> keys, BuiltinExchangeType type) {
        this.channel = channel;
        this.consumer = defaultConsumer();
        this.exchange = exchange;
        this.type = type;
        this.queues = queues;
        this.keys = keys;
    }
    public RabbitConsumer(Channel channel, String exchange, List<String> queues, List<String> keys, BuiltinExchangeType type, Consumer consumer){
        this.channel = channel;
        this.consumer = consumer;
        this.exchange = exchange;
        this.type = type;
        this.queues = queues;
        this.keys = keys;
    }
    public void init() throws IOException {
        channel.exchangeDeclare(exchange,type);
        initQueues();
    }

    private void initQueues() throws IOException {
        for(int i=0;i<queues.size();i++){
            String queue = queues.get(i);
            String key = keys.get(i);
            channel.queueDeclare(queue, false, false, false, null);
            channel.queueBind(queue,exchange, key);
            channel.basicConsume(queue, true, consumer);
        }

    }

    private Consumer defaultConsumer(){
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

            }
        };
    }


}
