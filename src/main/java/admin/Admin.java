package admin;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import Config.Config;
import util.RabbitConnection;
import util.RabbitConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        channel.exchangeDeclare(Config.LOG_EXCHANGE, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare(Config.INFO_EXCHANGE, BuiltinExchangeType.FANOUT);
        new RabbitConsumer(channel,
                Config.LOG_EXCHANGE,
                Arrays.asList(Config.LOG_QUEUE),
                Arrays.asList("#"),
                BuiltinExchangeType.FANOUT)
                .init();

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // read msg
            System.out.println("Enter info: ");
            String info = br.readLine();


            // break condition
            if ("exit".equals(info)) {
                break;
            }

            // publish
            channel.basicPublish(Config.INFO_EXCHANGE, info, null, info.getBytes("UTF-8"));
            System.out.println("Sent: " + info);
        }


    }
}
