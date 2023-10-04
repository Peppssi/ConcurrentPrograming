package actor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {

    private BlockingQueue<M> q = new LinkedBlockingQueue<M>();

    /** Called by another thread, to send a message to this thread. */
    public void send(M message) throws InterruptedException {
        q.put(message);
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        return q.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        return q.poll(timeout, TimeUnit.MILLISECONDS);
    }
}