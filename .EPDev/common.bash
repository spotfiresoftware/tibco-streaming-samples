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
    ${MVN} -B -e "${@}"
}

# Get a property of a single project.
get_project_property() {
    run_maven help:evaluate -Dexpression="$1" -q -DforceStdout
}

# Get the current parent version of the reference project.
get_current_parent_version() {
    cd ${reference_project} && get_project_property 'project.parent.version'
}
