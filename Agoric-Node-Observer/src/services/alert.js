const {Webhook, MessageBuilder} = require('discord-webhook-node')

const config = require('../config')

const twilio = require('twilio')(config.twilioAccountSid, config.twilioAuthToken);

let discordWebhook

const debugMode = config.debugMode
const contacts = config.emergencyCallContacts

async function sendDiscordMsg(appName, level, msg) {
    let logLevel;
    let isDebug;

    switch (level) {
        case config.ALERT_LEVEL_INFO:
            logLevel = "INFO"
            break;
        case config.ALERT_LEVEL_WARN:
            logLevel = "WARNING"
            break;
        case config.ALERT_LEVEL_URGENT:
            logLevel = "URGENT"
            break;
    }

    if (discordWebhook == null) discordWebhook = new Webhook(config.discordWebhookUri)

    if (debugMode) {
        isDebug = "[Debug]"
    } else {
        isDebug = ""
    }

    try {
        switch (level) {
            case config.ALERT_LEVEL_INFO:
                await discordWebhook.info(`${logLevel} ${isDebug}`, appName, msg);
                break
            case config.ALERT_LEVEL_WARN:
                await discordWebhook.warning(`${logLevel} ${isDebug}`, appName, msg);
                break
            case config.ALERT_LEVEL_URGENT:
                await discordWebhook.error(`${logLevel} ${isDebug}`, appName, msg);
                break
        }

    } catch (e) {
        console.log(e)
    }
}

async function emergencyCallTwilio() {
    if (!config.emergencyCallEnabled) return

    contacts.forEach(function (entry) {
        callTwilio(entry.contact)
    })
}

async function callTwilio(number) {
    twilio.calls
        .create({
            url: 'http://demo.twilio.com/docs/voice.xml',
            to: number,
            from: config.twilioCallerPhoneNumber
        }).catch(e => {
        console.log(`error = ${e}`)
    })
        .then(call => {
            console.log(call)
            return "success!"
        })
}

module.exports = {
    emergencyCallTwilio,
    discordAlert: function (appName, level, msg) {
        sendDiscordMsg(appName, level, msg).catch(e => {
        }).then()
    }
}