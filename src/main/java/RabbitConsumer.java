import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;

public class RabbitConsumer {
    private final Channel channel;
    private final Consumer consumer;
    private final String exchange;

    public RabbitConsumer(Channel channel, String exchange, List<String> queues, List<String> keys, BuiltinExchangeType type) throws IOException {
        this.channel = channel;
        this.consumer = defaultConsumer();
        this.exchange = exchange;

        channel.exchangeDeclare(exchange,type);
        initQueues(queues,keys);
    }
    public RabbitConsumer(Channel channel, String exchange, List<String> queues, List<String> keys, BuiltinExchangeType type, Consumer consumer) throws IOException {
        this.channel = channel;
        this.consumer = consumer;
        this.exchange = exchange;

        channel.exchangeDeclare(exchange,type);
        initQueues(queues,keys);
    }

    private void initQueues(List<String> queues, List<String> keys) throws IOException {
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
