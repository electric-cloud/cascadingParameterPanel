
// cascade.java --
//
// cascade.java is part of the cascadeParameterPlugin plugin.
//
// Copyright (c) 2005-2010 Electric Cloud, Inc.
// All rights reserved.
//

package ecplugins.cascadeParameterPanel.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.electriccloud.commander.client.ChainedCallback;
import com.electriccloud.commander.client.domain.ActualParameter;
import com.electriccloud.commander.client.domain.FormalParameter;
import com.electriccloud.commander.client.domain.ObjectType;
import com.electriccloud.commander.client.domain.Project;
import com.electriccloud.commander.client.domain.Procedure;
import com.electriccloud.commander.client.requests.FindObjectsFilter.IsNullFilter;
import com.electriccloud.commander.client.requests.FindObjectsRequest;
import com.electriccloud.commander.client.requests.GetProceduresRequest;
import com.electriccloud.commander.client.responses.CommanderError;
import com.electriccloud.commander.client.responses.FindObjectsResponse;
import com.electriccloud.commander.client.responses.FindObjectsResponseCallback;
import com.electriccloud.commander.client.responses.ProcedureListCallback;
import com.electriccloud.commander.gwt.client.ComponentBase;
import com.electriccloud.commander.gwt.client.ui.ParameterPanel;
import com.electriccloud.commander.gwt.client.ui.ParameterPanelProvider;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.electriccloud.commander.gwt.client.ui.FormTable;


/**
 * Basic component that is meant to be cloned and then customized to perform a
 * real function.
 */
public class cascadeParameterPanel
    extends ComponentBase
    implements ParameterPanel,
    ParameterPanelProvider
{
    //~ Static fields/initializers ---------------------------------------------
	static final String boxWidth = "300px";

    // These are all the formalParameters on the Procedure
    static final String PROJECT   = "projName";
    static final String PROCEDURE = "procName";

    // These are the UI elements in the parameter panel
    private FormTable           m_form;
	ListBox                     projectLB;		// Menu select for list of Projects
	ListBox						procedureLB;	// Menu select for list of procedures
	String                      selectedProject="";

    // Holds the response of the Commander FindObjects Request
    private List<Project>       m_cmdrProjectResponse;
 
    private Map<String, FormalParameter> m_formalParams;
	private Map<String, String>          m_actualParams;

    //~ Methods ----------------------------------------------------------------

    /**
     * This function is called by SDK infrastructure to initialize the UI parts of
     * this component.
     *
     * @return                 A widget that the infrastructure should place in
     *                         the UI; usually a panel.
     */
    @Override public Widget doInit()
    {
        // Create a FindObjectsRequest for projects
        FindObjectsRequest findProjects = this.getRequestFactory()
                .createFindObjectsRequest(
                        ObjectType.project);

        // Set maximum number of results returned by the request
        findProjects.setMaxIds(100);

        // Filter results
        IsNullFilter filter=new IsNullFilter("pluginName");
        findProjects.addFilter(filter);
        
        // Set the callback
        findProjects.setCallback(new FindObjectsResponseCallback() {
            @Override public void handleError(CommanderError error)
            {

                // There was an error, create an empty List.
                m_cmdrProjectResponse = new ArrayList<Project>();
            }

            @Override public void handleResponse(
                    FindObjectsResponse response)
            {
                m_cmdrProjectResponse = response.getProjects();
            }
        });

        this.doRequest(new ChainedCallback() {
            @Override
            public void onComplete() {
                if (m_cmdrProjectResponse.size() > 1) {
                	Collections.sort(m_cmdrProjectResponse, new Comparator<Project>() {
                        @Override
                        public int compare(final Project object1, final Project  object2) {
                            return object1.getName().compareTo(object2.getName());
                        }
                    } );
                }                		
                projectLB.addItem("");
                for (Project currentProject : m_cmdrProjectResponse) {
                    projectLB.addItem(currentProject.getName());
                }

                // Populate the form based on if we are editing an existing object or
                // creating a new object
                if (m_actualParams != null && m_actualParams.size() > 0) {

                    // Set the Project
                    final String projectValue = m_actualParams.get(PROJECT);

                    for (int i = 0; i < projectLB.getItemCount(); i++) {

                        if (projectLB.getValue(i)
                                .equals(projectValue)) {
                            projectLB.setValue(i, projectValue);
                        }
                    }
                    projectLB.setSelectedIndex(0);
                }
            }
        }, findProjects);

        m_form     = getUIFactory().createFormTable();
        
        projectLB   = new ListBox(false);
        projectLB.setWidth(boxWidth);
        projectLB.addChangeHandler(new ChangeHandler()
        {
        	public void onChange(ChangeEvent event) 
        	{
        		int index=projectLB.getSelectedIndex();
        		if (index >= 1) {
        			selectedProject=projectLB.getValue(index);
        			getProcedures(selectedProject, procedureLB);
        		}
        	}
					
        });

        procedureLB = new ListBox(false);
        procedureLB.setWidth(boxWidth);
        procedureLB.setVisibleItemCount(10);
        // Change Handle for selecting a procedure
       procedureLB.addChangeHandler(new ChangeHandler()
        {
        	public void onChange(ChangeEvent event) 
        	{
        		int index=procedureLB.getSelectedIndex();
        	}
 					
        });

       m_form.addFormRow("1", "Project:", projectLB, true,
               "Choose the project.");

       m_form.addFormRow("2", "Procedures: ", procedureLB, false,
               "Choose the procedures");

        // Fill in the caption panel...
        // We're done setting up the UI.  Return the panel.
       return m_form.asWidget();
    }

    /**
     * Gets the values of the parameters that should map 1-to-1 to the formal
     * parameters on the object being called. Transform user input into a map of
     * parameter names and values.
     *
     * <p>This function is called after the user hits submit and validation has
     * succeeded.</p>
     *
     * @return  The values of the parameters that should map 1-to-1 to the
     *          formal parameters on the object being called.
     */
	@Override
	public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<String, String>();
        String procedureList = "";
        
        // Extract Selected Project 
        values.put(PROJECT,
                projectLB.getValue(projectLB.getSelectedIndex()));
        
        // Extract Selected Procedure
        values.put(PROCEDURE, procedureLB.getValue(procedureLB.getSelectedIndex()));

        return values;
	}

    /**
     * Straight forward function usually just return this;
     *
     * @return  straight forward function usually just return this;
     */
    @Override public ParameterPanel getParameterPanel()
    {
        return this;
    }

    /**
     * Push actual parameters into the panel implementation.
     *
     * <p>This is used when editing an existing object to show existing
     * content.</p>
     *
     * @param  actualParameters  Actual parameters assigned to this list of
     *                           parameters.
     */
    @Override public void setActualParameters(
            Collection<ActualParameter> actualParameters)
    {

        // Store actual params into a hash for easy retrieval later
        m_actualParams = new HashMap<String, String>();

        for (ActualParameter actualParameter : actualParameters) {
            m_actualParams.put(actualParameter.getName(),
                actualParameter.getValue());
        }
    }

    /**
     * Push form parameters into the panel implementation.
     *
     * <p>This is used when creating a new object and showing default values.
     * </p>
     *
     * @param  formalParameters  Formal parameters on the target object.
     */
    @Override public void setFormalParameters(
            Collection<FormalParameter> formalParameters)
    {
        m_formalParams = new HashMap<String, FormalParameter>();

        for (FormalParameter formalParameter : formalParameters) {
            m_formalParams.put(formalParameter.getName(), formalParameter);
        }
    }
    /**
     * Performs validation of user supplied data before submitting the form.
     *
     * <p>This function is called after the user hits submit.</p>
     *
     * @return  true if checks succeed, false otherwise
     */
    @Override public boolean validate()
    {
        //Check to see if a project has been selected
    	if (projectLB.getSelectedIndex() == -1) {
    		m_form.setErrorMessage("1", "Project selection required");
    		return false;
    	}
    	if (procedureLB.getSelectedIndex() == -1) {
    		m_form.setErrorMessage("1", "Procedure selection required");
    		return false;
    	}
         return true;
    }

    private void getProcedures(final String projectStr, final ListBox lb) {
        // let's get the procedures associated to this Project
        GetProceduresRequest getProcedureReq = getRequestFactory().createGetProceduresRequest();
        getProcedureReq.setProjectName(projectStr);
        getProcedureReq.setCallback(new ProcedureListCallback() {
            @Override   public void handleError(CommanderError error) {
                getLog().debug("Error code=" + error.getCode() + ", Error message=" + error.getMessage());
            }
            @Override  public void handleResponse(List<Procedure> response) {
                lb.setEnabled(true);
                lb.clear();
                if (response.size() > 1) {
                	Collections.sort(response, new Comparator<Procedure>() {
                        @Override
                        public int compare(final Procedure object1, final Procedure  object2) {
                            return object1.getName().compareTo(object2.getName());
                        }
                    } );
                }                		

                for (Procedure currentProcedure:response) {
                    lb.addItem(currentProcedure.getName());
                    
                }
            } // handleResponse
        });

        // now go get all properties ...
        doRequest(new ChainedCallback() {
            @Override public void onComplete()
            {
            }
        }, getProcedureReq);        
    }
}
