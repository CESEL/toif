"""
The MIT License (MIT)

Copyright (c) 2015 Louis-Philippe Querel, Concordia University CESEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
"""

import json
import os
from git import Repo
from tools.git_util import git_util

__author__ = 'lquerel'

import sys

# We need to be able to reuse the existing code
# Go through all commits
# Compare to existing existing runs to only add new commits

TOOL_BASE_PATH = ".git/cesel/churns"
CHURNS_RAW_FILE = "preprocessed.json"
CHURNS_PROCESSED_FILE = "processed.json"
CHURN_VERSION = 0.311

PROCESSED_COMMIT_TEMPLATE = {"commits": [], "files": {}, "version": CHURN_VERSION}


def __validate_dir_path__(path):
    if not os.path.exists(path):
        raise Exception("Provided path '%s' does not exist. Please confirm the path" % path)
    if not os.path.isdir(path):
        raise Exception("Provided path '%s' is not a directory. Please ensure that you are pointing to a directory" % path)


def __get_file_path__(path, file_name):
    return os.path.join(path, os.path.join(TOOL_BASE_PATH, file_name))


def __get_processed_content__(path):

    processed_file_path = __get_file_path__(path, CHURNS_PROCESSED_FILE)

    if not os.path.exists(processed_file_path):
        print "Running on a new repository"
        return PROCESSED_COMMIT_TEMPLATE
    else:
        # Load the file and check that it meets the format that is expected
        f = open(processed_file_path, 'r')
        content = f.readlines()
        f.close()

        # Validate that the content was valid

        if len(content) > 0:
            content = json.loads(content[0])

        # Validate version of data. If it is the same version of schema we want to reuse it otherwise it should be overwritten
        if "version" not in content:
            return PROCESSED_COMMIT_TEMPLATE
        elif content['version'] < CHURN_VERSION:
            print "Older version of churn tool was previously use which had a different storage format. Will reprocess data"
            return PROCESSED_COMMIT_TEMPLATE
        elif content['version'] > CHURN_VERSION:
            print "A newer version of this tool as been used on this project. Are you sure that you want to run this older version?"
            sys.exit(1)
        else:
            return content


def __save_processed_content__(path, content):

    tool_data_directory = os.path.join(path, TOOL_BASE_PATH)
    if not os.path.exists(tool_data_directory):
        os.makedirs(tool_data_directory)

    f = open(__get_file_path__(path, CHURNS_PROCESSED_FILE), 'w')
    f.write(json.dumps(content))
    f.close()


def churns(git_repo_path):

    print "Processing path '%s'" % git_repo_path
    # Check if the file path exists
    __validate_dir_path__(git_repo_path)

    #Get the existing list of processed commits
    existing_processed_commits = __get_processed_content__(git_repo_path)
    # Get the list of commits from the repository and their metrics
    git = git_util(git_repo_path, existing_processed_commits)
    git_commit, git_dag, git_measurements = git.get_commits()


    file_churns = {}
    processed_churn_data = existing_processed_commits
    processed_churn_data["commits"] = processed_churn_data["commits"] + git_measurements.keys()

    # Aggregate the churns to commits
    for commit_sha in git_measurements.keys():
        commit_measurements = git_measurements[commit_sha]
        for file_name in commit_measurements.keys():
            if file_name not in file_churns:
                file_churns[file_name] = {}

            file_churns[file_name][commit_sha] = commit_measurements[file_name]

            if file_name not in processed_churn_data['files']:

                processed_churn_data['files'][file_name] = {"file": file_name, "sum": commit_measurements[file_name]['lines'], "commits": [commit_sha]}
            else:
                processed_churn_data['files'][file_name]["sum"] += commit_measurements[file_name]['lines']
                processed_churn_data['files'][file_name]["commits"].append(commit_sha)

    processed_churn_data['file_list'] = []
    for file_name in processed_churn_data['files'].keys():
        processed_churn_data['file_list'].append(processed_churn_data['files'][file_name])

    print "Processed repository from current commit. %s new commits were added in addition to %s existing preprocessed commits" % ((len(processed_churn_data['commits'])-len(existing_processed_commits['commits'])), len(existing_processed_commits['commits']))

    __save_processed_content__(git_repo_path, processed_churn_data)

if __name__ == "__main__":
    import sys
    if len(sys.argv) < 2:
        print "File path to git repository needs to be provided"

    churns(sys.argv[1])
