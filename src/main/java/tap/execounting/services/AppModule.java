package tap.execounting.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.apache.tapestry5.validator.ValidatorMacro;

import tap.execounting.dal.HibernateModule;
import tap.execounting.dal.mediators.MediatorModule;
import tap.execounting.encoders.TeacherEncoder;
import tap.execounting.encoders.UserEncoder;
import tap.execounting.entities.Teacher;
import tap.execounting.entities.User;
import tap.execounting.models.beanmodels.ClientModels;
import tap.execounting.security.AuthenticationFilter;
import tap.execounting.security.AuthorizationDispatcher;
import tap.execounting.security.DispatcherOne;
import fr.exanpe.t5.lib.services.ExanpeLibraryModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule({ HibernateModule.class, MediatorModule.class,
		ExanpeLibraryModule.class, ValidationModule.class })
public class AppModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(Authenticator.class, BasicAuthenticator.class);
		binder.bind(BroadcastingService.class);
		binder.bind(AuthorizationDispatcher.class, DispatcherOne.class);
        binder.bind(ClientModels.class);
	}

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, Object> configuration) {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "ru");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
        configuration.add(SymbolConstants.HMAC_PASSPHRASE, "fjads;bbvnvn,m gkwo[k there are piss back;qlfkjqe;flkwjefw;lefkj");
    }

    public static void contributeClasspathAssetAliasManager(
            MappedConfiguration<String, String> configuration) {
        configuration.add("css", "assets/css");
        configuration.add("js", "assets/js");
    }

    @Contribute(ComponentRequestHandler.class)
    public static void contributeComponentRequestHandler(
            OrderedConfiguration<ComponentRequestFilter> configuration) {
        configuration.addInstance("RequiresLogin", AuthenticationFilter.class);
    }

	public static void contributeFactoryDefaults(
			MappedConfiguration<String, Object> configuration) {
		// configuration.override(SymbolConstants.APPLICATION_VERSION, "3.6");
		configuration.override(SymbolConstants.DATEPICKER, "assets/");
	}

    public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration){
        configuration.overrideInstance(Teacher.class, TeacherEncoder.class);
        configuration.overrideInstance(User.class, UserEncoder.class);
    }

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(
			MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=4, maxlength=15");
		configuration.add("password", "required, minlength=8, maxlength=12");
	}
}
