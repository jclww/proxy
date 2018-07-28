import com.lww.proxy.server.nettty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Run {
    private static final Logger logger = LoggerFactory.getLogger(Run.class);

    public static void main(String[] args) {
        NettyServer.main(args);
    }
}
