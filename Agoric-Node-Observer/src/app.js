const config = require('./config/index')
const alert = require('./services/alert.js')

const observer = require('./services/observer.js')

const CronJob = require('cron').CronJob

const MODULE_NAME = "Observer"

let prevStatus = {
    node_memory_MemTotal_bytes: 0,
    node_memory_MemAvailable: 0,
    node_filesystem_avail_bytes: 0,
    node_filesystem_size_bytes: 0,
    node_network_receive_drop_total_ens5: 0,
    node_network_receive_drop_total_lo: 0,
    node_network_receive_errs_total_ens5: 0,
    node_network_receive_errs_total_lo: 0
}

start();

function start() {
    startMonitoringSchedule()
    notification(config.ALERT_LEVEL_INFO, `Start monitoring...`).then()
}

function startMonitoringSchedule() {
    new CronJob(`*/${config.statusCheckInterval} * * * * *`, function () {
        checkNodeStatus().then()
    }).start()
}

async function checkNodeStatus() {
    let error
    const result = await observer.getNodeStatus().catch(e => {
        error = e
    })

    if (error) {
        await notification(config.ALERT_LEVEL_WARN, `Cannot check to node status.\n\n${error}`)
        return
    }

    var nodeStatus = result.split(/\r?\n/)

    var currentStatus = {
        node_memory_MemTotal_bytes: 0,
        node_memory_MemAvailable: 0,
        node_filesystem_avail_bytes: 0,
        node_filesystem_size_bytes: 0,
        node_network_receive_drop_total_ens5: 0,
        node_network_receive_drop_total_lo: 0,
        node_network_receive_errs_total_ens5: 0,
        node_network_receive_errs_total_lo: 0
    }

    nodeStatus.forEach(function (row) {
        if (row.includes("node_memory_MemTotal_bytes") && !row.includes("#")) {
            currentStatus.node_memory_MemTotal_bytes = (new Number(row.split(" ")[1]) / 1073741824)
        } else if (row.includes("node_memory_MemAvailable") && !row.includes("#")) {
            currentStatus.node_memory_MemAvailable = (new Number(row.split(" ")[1]) / 1073741824)
        } else if (row.includes("node_filesystem_avail_bytes") && row.includes(`mountpoint=\"${config.diskMountPoint}\"`) && !row.includes("#")) {
            currentStatus.node_filesystem_avail_bytes = (new Number(row.split(" ")[1]) / 1073741824)
        } else if (row.includes("node_filesystem_size_bytes") && row.includes(`mountpoint=\"${config.diskMountPoint}\"`) && !row.includes("#")) {
            currentStatus.node_filesystem_size_bytes = (new Number(row.split(" ")[1]) / 1073741824)
        } else if (row.includes("node_network_receive_drop_total") && !row.includes("#")) {
            if (row.includes("ens5")) currentStatus.node_network_receive_drop_total_ens5 = new Number(row.split(" ")[1])
            if (row.includes("lo")) currentStatus.node_network_receive_drop_total_lo = new Number(row.split(" ")[1])
        } else if (row.includes("node_network_receive_errs_total") && !row.includes("#")) {
            if (row.includes("ens5")) currentStatus.node_network_receive_errs_total_ens5 = new Number(row.split(" ")[1])
            if (row.includes("lo")) currentStatus.node_network_receive_errs_total_lo = new Number(row.split(" ")[1])
        }
    })

    if (prevStatus.node_memory_MemTotal_bytes == 0) {
        updateCurrentStatus(currentStatus)
        return
    }

    const prevAvailableMemRate = (prevStatus.node_memory_MemAvailable / prevStatus.node_memory_MemTotal_bytes * 100).toFixed(2)
    const availableMemRate = (currentStatus.node_memory_MemAvailable / currentStatus.node_memory_MemTotal_bytes * 100).toFixed(2)

    if (prevAvailableMemRate != availableMemRate && availableMemRate < config.minimumAvailableMemory) {
        await notification(config.ALERT_LEVEL_URGENT, `There is not enough memory available. \n\navailableMemRate : ${availableMemRate}%\nminimumAvailableMemory : ${config.minimumAvailableMemory}%`)
        console.log(`warning ${availableMemRate} / ${config.minimumAvailableMemory}`)
    }

    const prevAvailableDiskSpaceRate = (prevStatus.node_filesystem_avail_bytes / prevStatus.node_filesystem_size_bytes * 100).toFixed(2)
    const availableDiskSpaceRate = (currentStatus.node_filesystem_avail_bytes / currentStatus.node_filesystem_size_bytes * 100).toFixed(2)

    if (prevAvailableDiskSpaceRate != availableDiskSpaceRate && availableDiskSpaceRate < config.minimumAvailableDiskSpace) {
        await notification(config.ALERT_LEVEL_URGENT, `There is not enough disk space available. \n\navailableDiskSpace : ${availableDiskSpaceRate}%\nminimumAvailableDiskSpace : ${config.minimumAvailableDiskSpace}%`)
    }

    if (prevStatus.node_network_receive_drop_total_ens5 < currentStatus.node_network_receive_drop_total_ens5)
        await notification(config.ALERT_LEVEL_WARN, `node_network_receive_drop_total (ens5) has been increased. \n\nPrevious : ${prevStatus.node_network_receive_drop_total_ens5}\nCurrent : ${currentStatus.node_network_receive_drop_total_ens5}`)

    if (prevStatus.node_network_receive_drop_total_lo < currentStatus.node_network_receive_drop_total_lo)
        await notification(config.ALERT_LEVEL_WARN, `node_network_receive_drop_total (lo) has been increased. \n\nPrevious : ${prevStatus.node_network_receive_drop_total_lo}\nCurrent : ${currentStatus.node_network_receive_drop_total_lo}`)

    if (prevStatus.node_network_receive_errs_total_ens5 < currentStatus.node_network_receive_errs_total_ens5)
        await notification(config.ALERT_LEVEL_WARN, `node_network_receive_errs_total (ens5) has been increased. \n\nPrevious : ${prevStatus.node_network_receive_errs_total_ens5}\nCurrent : ${currentStatus.node_network_receive_errs_total_ens5}`)

    if (prevStatus.node_network_receive_errs_total_lo < currentStatus.node_network_receive_errs_total_lo)
        await notification(config.ALERT_LEVEL_WARN, `node_network_receive_errs_total (lo) has been increased. \n\nPrevious : ${prevStatus.node_network_receive_errs_total_lo}\nCurrent : ${currentStatus.node_network_receive_errs_total_lo}`)

    updateCurrentStatus(currentStatus)
}

function updateCurrentStatus(currentStatus) {
    prevStatus.node_memory_MemTotal_bytes = currentStatus.node_memory_MemTotal_bytes
    prevStatus.node_memory_MemAvailable = currentStatus.node_memory_MemAvailable
    prevStatus.node_filesystem_avail_bytes = currentStatus.node_filesystem_avail_bytes
    prevStatus.node_filesystem_size_bytes = currentStatus.node_filesystem_size_bytes
    prevStatus.node_network_receive_drop_total_ens5 = currentStatus.node_network_receive_drop_total_ens5
    prevStatus.node_network_receive_drop_total_lo = currentStatus.node_network_receive_drop_total_lo
    prevStatus.node_network_receive_errs_total_ens5 = currentStatus.node_network_receive_errs_total_ens5
    prevStatus.node_network_receive_errs_total_lo = currentStatus.node_network_receive_errs_total_lo
}

async function notification(level, msg) {
    if (level >= config.ALERT_LEVEL_URGENT) {
        await emergencyCall()
    }
    alert.discordAlert(MODULE_NAME, level, msg)
}

async function emergencyCall() {
    await alert.emergencyCallTwilio()
}