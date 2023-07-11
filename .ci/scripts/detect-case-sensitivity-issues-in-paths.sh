#!/bin/bash
set -e
shopt -s expand_aliases

XARGS_MAX_CMD_LENGTH=`echo | xargs --show-limits 2>&1 | awk '/POSIX upper limit on argument length/ {print $NF}'`

echo "Checking paths under '$PWD' for case related problems..."

alias sort_case_insensitive='sort -f'
alias uniq_show_grouped_duplicates='uniq -i --all-repeated=separate'
alias xargs_get_parent_dirs_in_batches='xargs -s $XARGS_MAX_CMD_LENGTH -d "\n" dirname'
alias git_get_dotgit_path='git rev-parse --git-dir'

GIT_WORKTREE_FILES_UNDER_CURRENT_DIR=`git ls-files`

GIT_WORKTREE_ROOT=`git_get_dotgit_path`
[[ $GIT_WORKTREE_ROOT == ".git" ]] || echo "WARNING: The command is not running in the git repository's root"

EXIT_CODE=0

FILES_WITH_SIMILAR_PATHS=`echo "$GIT_WORKTREE_FILES_UNDER_CURRENT_DIR" | sort_case_insensitive | uniq_show_grouped_duplicates`
if [ -n "$FILES_WITH_SIMILAR_PATHS" ]; then
  EXIT_CODE=1
  echo
  echo "ERROR: These file paths differ only by case:"
  echo "$FILES_WITH_SIMILAR_PATHS"
fi

DIRECTORIES_WITH_SIMILAR_PATHS=`echo "$GIT_WORKTREE_FILES_UNDER_CURRENT_DIR" | xargs_get_parent_dirs_in_batches | sort_case_insensitive | uniq | uniq_show_grouped_duplicates`
if [ -n "$DIRECTORIES_WITH_SIMILAR_PATHS" ]; then
  [ -n "$FILES_WITH_SIMILAR_PATHS" ] && echo -e "\n==================\n"
  EXIT_CODE=1
  echo "ERROR: These directory paths differ only by case:"
  echo "$DIRECTORIES_WITH_SIMILAR_PATHS"
fi

[ "$EXIT_CODE" -eq 0 ] && echo "SUCCESS: No case related problem found in '$PWD'."

exit $EXIT_CODE
