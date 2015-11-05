package tap.execounting.pages;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

@Import(stylesheet = { "context:/css/CRUD.css", "context:css/datatable.css" })
public class CRUD {

	@Persist
	@Property
	private String selectedTab;
	@Property
	private String tab;

	void onActivate() {
		if (selectedTab == null)
			selectedTab = "Клиенты";
	}

	public String[] getTabs() {
		return new String[] { "Клиенты", "Учителя", "Предметы",
				"Школы" };
	}

	void onSwitchTab(String tab) {
		selectedTab = tab;
	}

	public String getLast() {
		if (tab.equals("Школы"))
			return "last";
		return "";
	}

	public String getCssForLi() {
		if (tab.equals(selectedTab))
			return "activeMenuItem";
		return "";
	}
}
