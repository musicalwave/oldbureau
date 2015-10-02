package tap.execounting.encoders;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.ChainMap;
import tap.execounting.entities.Teacher;

import javax.inject.Inject;

/**
 * User: truth0
 * Date: 3/25/13
 * Time: 10:34 AM
 */
public class TeacherEncoder implements ValueEncoder<Teacher>, ValueEncoderFactory<Teacher> {

    @Inject
    private CRUDServiceDAO dao;

    @Override
    public String toClient(Teacher value) {
        return value.getName();
    }

    @Override
    public Teacher toValue(String clientValue) {
        return dao.findUniqueWithNamedQuery(Teacher.BY_NAME, ChainMap.with("name", clientValue));
    }

    @Override
    public ValueEncoder<Teacher> create(Class<Teacher> type) {
        return this;
    }
}
