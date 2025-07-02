pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'weather-prediction-service'
        DOCKER_TAG = "${env.BUILD_ID}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Stop and remove existing container
                    sh 'docker stop weather-service || true'
                    sh 'docker rm weather-service || true'

                    // Run new container
                    sh "docker run -d -p 8080:8080 --name weather-service ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }
    }

    post {
        always {
            // Clean up workspace
            cleanWs()
            // Archive test results
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}