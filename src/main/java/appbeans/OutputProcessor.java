package appbeans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public interface OutputProcessor extends Serializable{

    public List<Object> getValues(List<Object> objects);

}
