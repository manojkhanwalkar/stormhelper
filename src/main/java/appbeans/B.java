package appbeans;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class B implements Processor {

    @Override
    public void process() {
        System.out.println(this);
    }
}
