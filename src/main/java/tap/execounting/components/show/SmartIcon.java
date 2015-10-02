package tap.execounting.components.show;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.ioc.annotations.Inject;

public class SmartIcon {

	public static final String DELETED = "deleted";

	@Parameter(required = true)
	private String iconType;
	@Inject
	private ComponentResources resources;
	@Inject
	@Path("context:/icons/trash_16.png")
	private Asset deletedIcon;

	void beginRender(MarkupWriter writer) {
		if (iconType == null)
			return;
		if (iconType.equals(DELETED)) {
			writer.element("img").attribute("alt", "deletedIcon")
					.attribute("src", deletedIcon.toClientURL());
			writer.end();
		}

	}
}
