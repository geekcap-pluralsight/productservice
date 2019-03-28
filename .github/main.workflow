workflow "New workflow" {
  resolves = ["Test"]
  on = "pull_request"
}

action "Test" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "clean test"
}
