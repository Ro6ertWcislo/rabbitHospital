package admin;

import com.rabbitmq.client.*;
import Config.Config;
import org.slf4j.LoggerFactory;
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
    private final org.slf4j.Logger log;

    public Admin() throws IOException, TimeoutException {
        this.connection = new RabbitConnection();
        this.channel = connection.getChannel();
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

         log = LoggerFactory.getLogger(Admin.class);
    }

    public void run() throws IOException {
        channel.exchangeDeclare(Config.LOG_EXCHANGE, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare(Config.INFO_EXCHANGE, BuiltinExchangeType.FANOUT);
        new RabbitConsumer(channel,
                Config.LOG_EXCHANGE,
                null,
                null,
                BuiltinExchangeType.FANOUT,
                getAdminConsumer())
                .init();

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // read msg
            System.out.println("Enter info: ");
            String info = br.readLine();
            log.info(info);


            // break condition
            if ("exit".equals(info)) {
                break;
            }

            // publish
            channel.basicPublish(Config.INFO_EXCHANGE, info, null, info.getBytes("UTF-8"));
            System.out.println("Sent: " + info);
        }


    }
    public Consumer getAdminConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.info(message);
            }
        };
    }
}
