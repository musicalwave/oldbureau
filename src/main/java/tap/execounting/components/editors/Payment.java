package tap.execounting.components.editors;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;
import tap.execounting.dal.mediators.interfaces.PaymentMed;

/**
 * User: truth0
 * Date: 4/7/13
 * Time: 4:12 PM
 */
public class Payment {
    @InjectComponent
    Zone zone;

    // View model:
    @Parameter @Property
    private Integer contractId;
    @Property
    private Integer conId;
    @Parameter @Property
    private Integer paymentId;
    @Property
    private boolean editMode;
    @Property
    tap.execounting.entities.Payment pay;

    void onPrepareForRenderFromEditor(){
        pay = med.getUnitById(paymentId);
    }

    public Object onAdd(int contractId){
        editMode = true;
        conId = contractId;
        return zone.getBody();
    }

    void onSuccessFromAdder(int contractId){
        pay.setContractId(contractId);
        med.create(pay);
        editMode = false;

        resources.triggerEvent("RefreshPaymentZone", new Object[]{contractId}, null);
    }

    // Somehow payment looses the id and contractId, after edit.
    void onSuccessFromEditor(int paymentId){
        tap.execounting.entities.Payment oldPay = med.getUnitById(paymentId);
        pay.setId(oldPay.getId());
        pay.setContractId(oldPay.getContractId());

        med.update(pay);

        resources.triggerEvent("RefreshPaymentZoneEditor", new Object[]{paymentId}, null);
    }

    // App infra:
    @Inject
    PaymentMed med;
    @Inject
    ComponentResources resources;
}
