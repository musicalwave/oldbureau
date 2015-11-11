package tap.execounting.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import tap.execounting.dal.mediators.interfaces.ContractMed;
import tap.execounting.util.DateUtil;

import java.util.Date;

public class Freezer {
	// Bits
	@Inject
	private AjaxResponseRenderer renderer;
	@Inject
	private ContractMed contractMed;
	@Inject
	private ComponentResources resources;
	
	// Screen fields
	@Component
	private Zone zona;
	@Property
	private Date dateFreeze;
	@Property
	private Date dateUnfreeze;
	
	// Behavior fields
	@Property
	private boolean active;
	@Property
	private int contractId;
	
	void onPrepare(){
		dateFreeze = new Date();
		dateUnfreeze = DateUtil.datePlusMonths(dateFreeze, 6);
	}
	
	public void activate( int contractId){
        this.contractId = contractId;
		active = true;
	}
	
	public Zone getZone(){
		return zona;
	}
	
	void onSubmit(){
		CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>();
		resources.triggerEvent("SuccessfullFreeze", new Object[]{new Integer(contractId)}, callback);
	}
	void onSuccess(){
		contractMed.doFreeze(dateFreeze, dateUnfreeze);
		active = false;
		renderer.addRender(getZoneID(), zona);
		CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>(); 
		resources.triggerEvent("SuccessfullFreeze", new Object[]{contractId}, callback);
	}
	
	public String getZoneID(){
		return "freezr"+contractId;
	}
}
