#!/usr/bin/bash

set -e

function getDependencyListFromPOM {
    if [ "$#" -ne 2 ]; then
        echo "Error: Function ${FUNCNAME[0]} requires 2 arguments. Got $# arguments"
        exit 22;
    fi
    cd ${PROJECT_DIR}
    ./mvnw -Dmaven.repo.local=${HOME}/.m2/repository -q -Djava.awt.headless=true -f "${1}" -B dependency:list  -Dsilent=true -Dsort=true -DoutputFile="${2}" -DoutputAbsoluteArtifactFilename > /dev/null 2>&1
}

# Variable Definitions
DEPENDENCY_CHECK_SCRIPT=${1}
FAIL_BUILD_ON_CVSS=${2}
SUPPRESSION_FILE=${3}
FORK_BRANCH=${4}
BASE_DIR=$(pwd)
PROJECT_DIR="$BASE_DIR/api"

SOURCE_BRANCH_POM="${PROJECT_DIR}/pom.xml"
TARGET_BRANCH_POM="${PROJECT_DIR}/main_pom.xml"
DEP_DIFF_FILE="${PROJECT_DIR}/deps.diff.txt"

cd ${BASE_DIR}
echo "Getting original pom"
FORK_HASH=$(git merge-base --fork-point $FORK_BRANCH)
echo retrieving pom.xml from branch $FORK_BRANCH forking point $FORK_HASH
git show $FORK_HASH:api/pom.xml > ${TARGET_BRANCH_POM}
cd -

echo diffing pom.xml
diff ${SOURCE_BRANCH_POM} ${TARGET_BRANCH_POM} && { echo "POM files didn't change! exiting"; exit 0; }

echo "Getting deps list from target"
# Get Dependency list from poms and diff them
getDependencyListFromPOM "${TARGET_BRANCH_POM}" "${TARGET_BRANCH_POM}.txt"
echo "Getting deps list from source"
getDependencyListFromPOM "${SOURCE_BRANCH_POM}" "${SOURCE_BRANCH_POM}.txt"

ls -la
cat ${TARGET_BRANCH_POM}.txt
cat ${SOURCE_BRANCH_POM}.txt

git diff --no-index -U0 ${TARGET_BRANCH_POM}.txt ${SOURCE_BRANCH_POM}.txt | grep "^\+" > $DEP_DIFF_FILE || true

echo diff between the poms
cat $DEP_DIFF_FILE

echo -e "Getting deps list!\n"
# Get Dependency list from poms and diff them
if [ ! -s $DEP_DIFF_FILE ]; then
    echo -e "POM changed but no dependencies added!\n"
    exit 0;
fi

echo new deps are:
mkdir -p /tmp/new_dependencies
grep -Eo '\/.*\.jar' $DEP_DIFF_FILE | xargs -I '{}' cp -v '{}' /tmp/new_dependencies
echo FAIL_BUILD_ON_CVSS=$FAIL_BUILD_ON_CVSS
bash $DEPENDENCY_CHECK_SCRIPT -s "/tmp/new_dependencies/**/*.jar" \
    --format JSON --format HTML --prettyPrint  --failOnCVSS $FAIL_BUILD_ON_CVSS --noupdate --data ${PROJECT_DIR}/dependency-check-database/ \
    --disableAssembly --disableNugetconf --disableNuspec --disableRetireJS --suppression $SUPPRESSION_FILE
