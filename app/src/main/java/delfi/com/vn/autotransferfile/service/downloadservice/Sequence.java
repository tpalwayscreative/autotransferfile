package delfi.com.vn.autotransferfile.service.downloadservice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PC on 9/5/2017.
 */

public class Sequence {
    private static final AtomicInteger counter = new AtomicInteger();

    public static int nextValue() {
        return counter.getAndIncrement();
    }

}
