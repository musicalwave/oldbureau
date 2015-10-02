var Authorize = Class.create();
Authorize.prototype = {
	initialize : function(element, message) {
		this.message = message;
		Event.observe($(element), 'click', this.doAlert
				.bindAsEventListener(this));
	},

	doAlert : function(e) {
		e.stop();
		alert(this.message);
	}
}