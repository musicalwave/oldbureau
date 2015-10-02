package tap.execounting.components.show;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import tap.execounting.dal.mediators.interfaces.PaymentMed;
import tap.execounting.security.AuthorizationDispatcher;

/**
 * User: truth0
 * Date: 4/7/13
 * Time: 12:54 PM
 * I thought it would be nice to make new payment.
 *
 * MiniPayment could display the information about any payment, just by id.
 * MiniPayment allows editing and deletion, but in that case, parent component
 * should be ready to handle RefreshMiniPaymentZone event after payment was edited.
 *
 * Should be handled:
 *  RefreshMiniPayment (int paymentId) -- update the property expression, that
 *  gave the paymentId to this component at the first time.
 */
public class MiniPayment {
    // View model code:
    @Parameter @Property
    private Integer paymentId;
    @Property
    private Integer payId;
    @Property
    private boolean editMode;
    @InjectComponent
    Zone zone;

    public String getPaymentInfo(){
        return med.getUnitById(paymentId).toString();
    }

    Object onEdit(int paymentId) {
        // AUTHORIZATION MOUNT POINT EDIT PAYMENT
        if (dispatcher.canEditPayments()) editMode = true;

        payId = paymentId;

        return zone.getBody();
    }

    boolean onDelete(int paymentId) {
        int contractId = med.getUnitById(paymentId).getContractId();

        // AUTHORIZATION MOUNT POINT DELETE PAYMENT
        if (dispatcher.canDeletePayments()) med.delete(paymentId);

        renderer.addRender("paymentZone" + paymentId, messages.get("has_been_deleted"));
        return true;
    }


    void onRefreshPaymentZoneEditor(int paymentId){
        editMode = false;

        resources.triggerEvent("RefreshMiniPaymentZone", new Object[]{paymentId}, null);

        renderer.addRender(zone);
    }

    // Images
    @Inject @Property
    @Path("context:icons/edit_16.png")
    private Asset editImg;
    @Inject @Property
    @Path("context:icons/trash_16.png")
    private Asset deleteImg;

    // App infrastructure:
    @Inject @Property
    private PaymentMed med;
    @Inject @Property
    private AuthorizationDispatcher dispatcher;

    // Tapestry infrastructure:
    @Inject
    ComponentResources resources;
    @Inject
    AjaxResponseRenderer renderer;
    @Inject
    Messages messages;
}
