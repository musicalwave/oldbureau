m = jQuery;

function rc() {
	var small = {
		mini : {
			fontSize : 0.8,
			margin : 3,
			width : 30,
			height : 30
		},
		date : {
			marginBottom : -0.45,
			marginTop : 22
		},
		dow : {
			marginLeft : 55,
			fontSize : 0.86
		},
		calendarMini : {
			width : 34,
			margin : 1,
			height : 34
		}
	}

	var big = {
		mini : {
			fontSize : 1,
			margin : 5,
			width : 46,
			height : 46
		},
		date : {
			marginBottom : -0.12,
			marginTop : 28
		},
		dow : {
			marginLeft : 57,
			fontSize : 0.97
		},
		calendarMini : {
			width : 50,
			margin : 3,
			height : 46
		}
	}

	var diff = {
		mini : {
			fontSize : big.mini.fontSize - small.mini.fontSize,
			margin : big.mini.margin - small.mini.margin,
			width : big.mini.width - small.mini.width,
			height : big.mini.height - small.mini.height
		},
		date : {
			marginBottom : big.date.marginBottom - small.date.marginBottom,
			marginTop : big.date.marginTop - small.date.marginTop
		},
		dow : {
			marginLeft : big.dow.marginLeft - small.dow.marginLeft,
			fontSize : big.dow.fontSize - small.dow.fontSize
		},
		calendarMini : {
			width : big.calendarMini.width - small.calendarMini.width,
			margin : big.calendarMini.margin - small.calendarMini.margin,
			height : big.calendarMini.height - small.calendarMini.height
		}
	}

	var coeff = 0.0;
	var winWidth = m(window).width();
	var etalonic = 1920.0;
	var etalSmall = 1360.0;

	if (winWidth >= etalonic)
		return;
	else if (winWidth >= etalSmall) {
		var biggestScaleDiff = etalonic - etalSmall;
		coeff = (winWidth - etalSmall) / biggestScaleDiff;
	}
	function ResizeMini() {
		fontSize = small.mini.fontSize + coeff * diff.mini.fontSize;
		margin = small.mini.margin + coeff * diff.mini.margin;
		width = small.mini.width + coeff * diff.mini.width;
		height = small.mini.height + coeff * diff.mini.width;
		m('.mini').css({
			'font-size' : fontSize + 'em',
			'margin' : margin + 'px',
			'width' : width + 'px',
			'height' : height + 'px',
			'transition' : 'all .4s ease'
		});
	}
	function ResizeDow() {
		marginLeft = small.dow.marginLeft + coeff * diff.dow.marginLeft;
		fontSize = small.dow.fontSize + coeff * diff.dow.fontSize;
		m('h4.dow').css({
			'font-size' : fontSize + 'em',
			'margin-left' : marginLeft + '%',
			'transition' : 'all .4s ease'
		});
	}
	function ResizeMiniDate() {
		mB = small.date.marginBottom + coeff * diff.date.marginBottom;
		mT = small.date.marginTop + coeff * diff.date.marginTop;
		m('div.mini h3.date').css({
			'margin-bottom' : mB + 'em',
			'margin-top' : mT + '%',
			'transition' : 'all .4s ease'
		});
	}
	function ResizeCalendar() {
		w = small.calendarMini.width + coeff * diff.calendarMini.width;
		h = small.calendarMini.height + diff.calendarMini.height * coeff;
		margin = small.calendarMini.margin + diff.calendarMini.margin * coeff;
		m('div.calendar div.mini').css({
			'width' : w + 'px',
			'margin' : margin + 'px',
			'height' : h + 'px',
			'transition' : 'all .4s ease'
		});
	}
	ResizeMini();
	ResizeDow();
	ResizeMiniDate();
	ResizeCalendar();
}

function linksHandle() {
	// Get links
	// if they are not ready -- wait for 300ms then call self again
	// If they are ready -- run resize
	clientsLinks = m('div#clientsZone.t-zone a');
	if(clientsLinks==null){
		setTimeout(linksHandle, 300);
		return;
	}

	rc();
	clientsLinks.click(function(e) {
		setTimeout(rc, 300);
		setTimeout(rc, 1800);
	});
}

m(document).ready(rc);
m(document).ready(linksHandle);
m(window).resize(rc);