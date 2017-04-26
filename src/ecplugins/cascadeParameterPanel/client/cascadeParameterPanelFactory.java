
package ecplugins.cascadeParameterPanel.client;

import com.electriccloud.commander.gwt.client.ComponentContext;

import com.electriccloud.commander.gwt.client.Component;
import com.electriccloud.commander.gwt.client.ComponentBaseFactory;

/**
 * This factory is responsible for providing instances of the cascade
 * class.
 */
public class cascadeParameterPanelFactory
    extends ComponentBaseFactory
{
 
    //~ Methods ----------------------------------------------------------------

    @Override public Component createComponent(ComponentContext jso)
    {
        return new cascadeParameterPanel();
    }
}
