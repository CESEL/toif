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

from datetime import datetime
from git import Repo
from timezone import *

__author__ = 'lquerel'

class git_util:

    def __init__(self, git_path, existing_commits):
        self.git_path = git_path
        self.existing_commits = existing_commits

    def __is_not_existing_commit__(self, commit):
        return commit.hexsha not in self.existing_commits["commits"]

    def get_commits(self):

        repo = Repo(self.git_path)

        # Repository should not be empty
        assert not repo.bare

        # TODO iterate through the list of commits and do not process commits which have already been done

        # Getting the information from the first commit
        git_commit, git_dag, git_measurements, commit_parents = self.get_commit_info(repo, repo.head.commit, {}, {}, {}, None)

        parents = set(commit_parents)

        # Iterate through the list of parents of the commits
        while parents:
            parent_commit = parents.pop()

            if self.__is_not_existing_commit__(parent_commit):
                git_commit, git_dag, git_measurements, new_parents = self.get_commit_info(repo, parent_commit, git_commit, git_dag, git_measurements, None)
                parents.update(new_parents)

        return git_commit, git_dag, git_measurements


    def get_commit_info(self, repo, commit, git_commit, git_dag, git_measurements, aszz):
        #message
        parents = []

        if commit.hexsha not in git_commit and self.__is_not_existing_commit__(commit):

            git_dag, parents = get_git_dag_and_parents(git_dag, git_commit, commit)
            git_commit = get_git_commit(git_commit, commit)
            # git_measurements[commit.hexsha] = get_git_measurements(commit, parents, aszz)

            git_measurements[commit.hexsha] = commit.stats.files
            print "processing commit %s" % commit.hexsha


        return git_commit, git_dag, git_measurements, parents


def get_git_dag_and_parents(git_dag, git_commit, commit):
    git_dag[commit.hexsha] = []
    parents = []

    for parent in commit.parents:
        git_dag[commit.hexsha].append(parent.hexsha)

        if parent not in git_commit:
            parents.append(parent)

    return git_dag, parents


def get_git_commit(git_commit, commit):
    git_commit[commit.hexsha] = {
        "commit": commit.hexsha,
        "tree": commit.tree.hexsha,
        "author": "%s <%s>" % (commit.author.name, commit.author.email),
        "author_dt": datetime.fromtimestamp(commit.authored_date, tz=Offset(commit.author_tz_offset)),
        "auth_id": None,
        "committer" : "%s <%s>" % (commit.committer.name, commit.committer.email),
        "committer_dt": datetime.fromtimestamp(commit.committed_date, tz=Offset(commit.committer_tz_offset)),
        "com_id": None,
        #subject
        #num_child
        "num_parent" : len(commit.parents)
        #log

    }

    return git_commit

