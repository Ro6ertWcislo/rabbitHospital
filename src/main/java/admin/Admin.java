package admin;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import util.Config;
import util.RabbitConnection;
import util.RabbitConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Admin {
    private final RabbitConnection connection;
    private final Channel channel;

    public Admin() throws IOException, TimeoutException {
        this.connection = new RabbitConnection();
        this.channel = connection.getChannel();
    }

    public void run() throws IOException {
        new RabbitConsumer(channel,
                Config.LOG_EXCHANGE,
                Arrays.asList(Config.LOG_QUEUE),
                Arrays.asList("#"),
                BuiltinExchangeType.FANOUT)
                .init();


    }
}
