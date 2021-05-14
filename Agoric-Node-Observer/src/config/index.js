const dotenv = require('dotenv')
dotenv.config()

let defaultConfig = {
    debugMode: (process.env.DEBUG_MODE.toLowerCase() === 'true'),
    emergencyCallEnabled: (process.env.EMERGENCY_CALL_ENABLE.toLowerCase() === 'true'),
    emergencyCallContacts: JSON.parse(process.env.EMERGENCY_CALL_CONTACTS),
    regularNotificationTime: process.env.REGULAR_NOTIFICATION_TIME,
    statusCheckInterval: process.env.STATUS_CHECK_INTERVAL,
    // Twilio
    twilioAccountSid: process.env.TWILIO_ACCOUNT_SID,
    twilioAuthToken: process.env.TWILIO_AUTH_TOKEN,
    twilioCallerPhoneNumber: process.env.WTILIO_CALLER_PHONE_NUMBER,
    // Discord
    discordWebhookUri: process.env.DISCORD_WEBHOOK_URI,
    // Node
    nodeIp: process.env.NODE_IP,
    nodeExporterPort: process.env.NODE_EXPORTER_PORT,
    // Disk mount point for monitoring
    diskMountPoint: process.env.DISK_MOUNT_POINT,
    // Trigger
    minimumAvailableDiskSpace: process.env.MINIMUM_AVAILABLE_DISK_SPACE,
    minimumAvailableMemory: process.env.MINIMUM_AVAILABLE_MEMORY,
    // Alert level
    ALERT_LEVEL_INFO: 0,
    ALERT_LEVEL_WARN: 1,
    ALERT_LEVEL_URGENT: 2,
}

module.exports = Object.freeze(defaultConfig)