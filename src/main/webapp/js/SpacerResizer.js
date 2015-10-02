Event.observe(window, "resize", ResizeSpace);

function ResizeSpace() {
	var windowWidth = document.viewport.getDimensions().width;
	var mainMenu = $('mainmenu');
	var totalWidth = mainMenu.getWidth();

	var spacerOne = $('spacerOne');
	if (windowWidth < 1100) {
		spacerOne.setStyle({
			width : 160 + 'px'
		});
	} else
		spacerOne.setStyle({
			width : '40%'
		});

	var spacerTwo = $('spacerTwo');
	var spacerThree = $('spacerThree');
	var availWidth = totalWidth - spacerOne.getWidth() - spacerTwo.getWidth()
			- 1;
	if (availWidth < 0)
		availWidth = 0;
	spacerThree.setStyle({
		width : availWidth + 'px'
	});
}