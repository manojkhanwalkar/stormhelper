package appbeans;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class C implements Processor {

    @Override
    public void process() {
        System.out.println(this);
    }
}
