
project 'cascadingParameterPanel', {
  description = '''This project is used in conjunction of the  cascadingParameterPanel to demonstrate how to affect a widget based on a choice made in a different one.
In this case, choosing a project affects the list of procedures displayed
'''

  procedure 'testCascadingParameterPanel', {

    formalParameter 'procName', {
      required = '1'
      type = 'entry'
    }

    formalParameter 'projName', {
      required = '0'
      type = 'entry'
    }

    step 'echo', {
      command = '''echo "Project: $[projName]"
echo "Procedure: $[procName]"
'''
    }

    // Custom properties
    customType = 'cascadeParameterPanel-1.0/cascade'
  }
}
