workflow "New workflow" {
  on = "push"
  resolves = ["GitHub Action for Maven"]
}

action "Build" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "clean package sonar:sonar -Dsonar.projectKey=louisthomas_productservice -Dsonar.organization=louisthomas-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$SONAR_TOKEN"
  secrets = ["SONAR_TOKEN"]
}

action "Deploy to PWS" {
  needs = "Build"
  uses = "louisthomas/cloud-foundry-cli-action@master"
  secrets = ["PASSWORD"]
  env = {
    CF_API_ENDPOINT = "api.run.pivotal.io"
    ORG = "ExploPaaS2016"
    SPACE = "experimentations"
    USERNAME = "louis-thomas.lamontagne@desjardins.com"
    APP_NAME = "product-service-demo"
   }
 }
