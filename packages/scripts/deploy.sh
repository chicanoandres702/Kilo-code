#!/bin/bash
# Deploy Engineer - gcloud + firebase deployments
set -euo pipefail

# [Parent Feature/Milestone] Infrastructure
# [Subtask] Deploy to Google Cloud Platform and Firebase
# [Upstream] GitHub Actions -> [Downstream] Cloud Run / Firebase Hosting
# [Law Check] 45 lines | Passed Do It Check

DEPLOY_TARGET="${1:-cloudrun}"
PROJECT_ID="${GCP_PROJECT_ID:-}"

deploy_cloudrun() {
    [[ -z "$PROJECT_ID" ]] && { echo "❌ GCP_PROJECT_ID not set"; return 1; }
    
    echo "🚀 Deploying to Cloud Run..."
    gcloud run deploy kilo-api \
        --image gcr.io/"$PROJECT_ID"/kilo-api \
        --platform managed \
        --region "${GCP_REGION:-us-central1}" \
        --allow-unauthenticated
}

deploy_firebase() {
    [[ -z "${FIREBASE_PROJECT_ID:-}" ]] && { echo "❌ FIREBASE_PROJECT_ID not set"; return 1; }
    
    echo "🔥 Deploying to Firebase..."
    firebase deploy --only hosting,functions --project "$FIREBASE_PROJECT_ID"
}

# Main
case "$DEPLOY_TARGET" in
    cloudrun) deploy_cloudrun ;;
    firebase) deploy_firebase ;;
    *) echo "Usage: $0 [cloudrun|firebase]" ;;
esac