package tap.execounting.dal;

import java.util.HashMap;
import java.util.Map;

/**
 * NamedQuery parameter helper to create the querys
 * 
 * @author karesti
 */
public class ChainMap
{

    private Map<String, Object> parameters = null;

    private ChainMap(String name, Object value)
    {
        this.parameters = new HashMap();
        this.parameters.put(name, value);
    }

    public static ChainMap w(String name, Object value)
    {
        return new ChainMap(name, value);
    }

    public ChainMap n(String name, Object value)
    {
        this.parameters.put(name, value);
        return this;
    }

    public Map<String, Object> and(String name, Object value)
    {
        this.parameters.put(name, value);
        return this.parameters;
    }

    public static Map<String, Object> with(String fullname, Object value) {
        return new ChainMap(fullname, value).parameters;
    }
}
