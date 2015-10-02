package tap.execounting.encoders;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.User;

import javax.inject.Inject;

/**
 * User: truth0
 * Date: 3/25/13
 * Time: 10:34 AM
 */
public class UserEncoder implements ValueEncoder<User>, ValueEncoderFactory<User> {

    @Inject
    private CRUDServiceDAO dao;

    @Override
    public String toClient(User value) {
        return value.getFullname();
    }

    @Override
    public User toValue(String clientValue) {
        return dao.findUniqueWithNamedQuery(User.BY_FULLNAME, ChainMap.with("fullname", clientValue));
    }

    @Override
    public ValueEncoder<User> create(Class<User> type) {
        return this;
    }
}
