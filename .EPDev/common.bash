# Common tibco-streaming-samples maintenance code.

declare should_debug=false

if [ "${1}" == "--debug" ] ; then
    set -x
    shift
    # For additional debug actions.
    should_debug=true
fi

set -e

declare -r here=$(pwd)

# The significant version is the Streaming parent, not the sample project version.
# So we pick one project.
declare -r reference_project=structure/eventflow

# Honor environment varible MVN_OPTIONS for mvn command options.
declare -r MVN="mvn ${MVN_OPTIONS}"

if [ ! -f "${reference_project}/pom.xml" ] ; then
    echo "No reference project at ${reference_project}, which is needed to determine the current Streaming version."
    exit 1
fi

# Force certain profiles for complete active project coverage. See MAINTENANCE.md.
run_maven() {
    ${MVN} -PactiveAnsible -B -e "${@}"
}

# Get a property of a single project.
get_project_property() {
    run_maven -N -q -Dexec.executable='echo' -Dexec.args="\${${1}}" exec:exec
}

# Get property *expression* for a tree of projects. Needed because the exec mojo
# will not allow us to convert project.basedir as a String, as the value is
# a File.
get_project_tree_property_ex() {
    run_maven -q -Dexec.executable='echo' -Dexec.args="${1}" exec:exec
}

# Get the current parent version of the reference project.
get_current_parent_version() {
    cd ${reference_project} && get_project_property 'project.parent.version'
}
