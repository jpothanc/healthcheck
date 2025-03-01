#!/bin/bash

# Check if all required parameters are provided
if [ "$#" -lt 4 ]; then
    echo "Usage: $0 SERVICE_NAME USER_NAME SERVICE_PATH JAR_FILE [--recreate]"
    exit 1
fi

SERVICE_NAME=$1
USER_NAME=$2
SERVICE_PATH=$3
JAR_FILE=$4
ENV_VARS=$5
RECREATE=$6

echo "env vars: $ENV_VARS"

# Service file path
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"

# Check if service exists and --recreate flag is not set
if [ -f "$SERVICE_FILE" ] && [ "$RECREATE" != "--recreate" ]; then
    echo "Service already exists. Restarting the service..."
    systemctl restart "$SERVICE_NAME"
    echo "Service $SERVICE_NAME has been restarted."
    exit 0
fi

# Validate that the service path and JAR file exist
if [ ! -d "$SERVICE_PATH" ]; then
    echo "Error: Service path $SERVICE_PATH does not exist"
    exit 1
fi

if [ ! -f "$SERVICE_PATH/$JAR_FILE" ]; then
    echo "Error: JAR file $SERVICE_PATH/$JAR_FILE does not exist"
    exit 1
fi




# Create service file
cat > "$SERVICE_FILE" << EOF
[Unit]
Description=${SERVICE_NAME} Java Service
After=network.target

[Service]
Type=simple

User=${USER_NAME}
$([ -n "$ENV_VARS" ] && echo "Environment=${ENV_VARS}")
WorkingDirectory=${SERVICE_PATH}
ExecStart=/usr/bin/java -jar ${SERVICE_PATH}/${JAR_FILE}
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# Set proper permissions
chmod 644 "$SERVICE_FILE"

# Reload systemd daemon
systemctl daemon-reload

# Enable service to start on boot
systemctl enable "$SERVICE_NAME"

echo "Service $SERVICE_NAME has been created/updated successfully"