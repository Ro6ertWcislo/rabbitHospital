package admin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AdminRun {
    public static void main(String[] args) throws IOException, TimeoutException {
        new Admin().run();
    }
}
