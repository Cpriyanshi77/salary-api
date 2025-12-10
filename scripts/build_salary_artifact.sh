#!/bin/bash
set -eu pipefail

echo "=============================================="
echo "  Building salary-api artifact"
echo "=============================================="

ARTIFACT_DIR="salary_artifact"
JAR_NAME="salary-0.1.0-RELEASE.jar"

echo "→ Running Maven build (clean package)..."
mvn -B -DskipTests clean package

echo "→ Checking built JAR..."
if [ ! -f "target/${JAR_NAME}" ]; then
  echo "ERROR: JAR target/${JAR_NAME} not found."
  echo "       Check the version/name in pom.xml and update JAR_NAME in this script."
  exit 1
fi

echo "→ Preparing artifact directory: ${ARTIFACT_DIR}"
rm -rf "${ARTIFACT_DIR}"
mkdir -p "${ARTIFACT_DIR}"

echo "→ Copying runtime files into artifact directory..."
# Main JAR
cp "target/${JAR_NAME}" "${ARTIFACT_DIR}/"

# Main application config (if exists)
if [ -f "src/main/resources/application.yml" ]; then
  cp "src/main/resources/application.yml" "${ARTIFACT_DIR}/"
fi

# Migration config (if exists)
if [ -f "migration.json" ]; then
  cp "migration.json" "${ARTIFACT_DIR}/"
fi

# Migration files directory (if exists)
if [ -d "migrations" ]; then
  cp -r "migrations" "${ARTIFACT_DIR}/"
fi

echo "→ Creating runnable launcher script (run.sh)..."
cat << 'EOF' > "${ARTIFACT_DIR}/run.sh"
#!/bin/bash
set -euo pipefail

echo "=============================================="
echo "  Starting salary-api"
echo "=============================================="

JAR_NAME="salary-0.1.0-RELEASE.jar"

if [ ! -f "${JAR_NAME}" ]; then
  echo "ERROR: ${JAR_NAME} not found in current directory."
  exit 1
fi

# Default port 8081 as per systemd/service example
JAVA_OPTS=${JAVA_OPTS:-""}
SERVER_PORT=${SERVER_PORT:-8081}

echo "→ Using SERVER_PORT=${SERVER_PORT}"
echo "→ Running: java \$JAVA_OPTS -jar ${JAR_NAME} --server.port=\${SERVER_PORT}"

exec java $JAVA_OPTS -jar "${JAR_NAME}" --server.port="${SERVER_PORT}"
EOF

chmod +x "${ARTIFACT_DIR}/run.sh"

echo "→ Packaging artifact into tar.gz..."
tar -czf salary-artifact.tar.gz "${ARTIFACT_DIR}"

echo "=============================================="
echo "  Artifact created successfully!"
echo "  Folder : ${ARTIFACT_DIR}"
echo "  Tarball: salary-artifact.tar.gz"
echo "=============================================="
