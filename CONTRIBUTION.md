# CONTRIBUTION

## Git
Commits should strive to follow the [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) format (merge commits nonwithstanding). Idealy each commit should at least be compileable and runnable as well, but with refactors, it can be understandable if this is not possible. A commit should represent a meaningful change, so "wip" commits are discouraged, and we encourage to squash and/or rename commits as needed.

### PRs
Pull requests should be up-to-date with the target branch at review-time. Rebases are encouraged, but if target-branch merge commits are permitted as well when appropriate.

New PRs based on `main` should target `main`. If they are based on `dev`, they should target `dev`.

### Branching Architecture
`main` is the primary release branch. All commits on `main` should be compileable, executable, bug-free (as much as possible) and "release ready".
`dev` is the staging branch. All commits on `dev` should be compileable and executable. Bugs are allowed and things are allowed to break here.
Use `feat/some-feature` for new features and `fix/some-fix` for new bugfixes. If you cant think of a good name for your branch, just give it some name to begin with, we can always rename the branch :-).

When creating new branches/forks, please base on either `main` or `dev`, note however that `dev` is rapidly changing so we recommend to fork from `main` by default.

## Code Quality
Generally, try to follow the style that is already present and try to reuse existing components when appropriate.

### Documentation
Ideally, any `public` thing should have a javadoc comment.
