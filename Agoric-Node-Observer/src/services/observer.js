const axios = require("axios")
const config = require('../config')

const request = async (targetUrl) => {
    try {
        return await axios.get(targetUrl);
    } catch (error) {
        throw new Error(error)
    }
}

async function getNodeStatus() {
    const targetUrl = `http://${config.nodeIp}:${config.nodeExporterPort}/metrics`
    let result = await request(targetUrl)
        .then(response => {
            return response.data
        }).catch(e => {
            throw new Error(`observer.js ${e}`)
        })

    return result
}

module.exports = {
    getNodeStatus,
}