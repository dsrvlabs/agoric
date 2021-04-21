## Agoric Node Observer
Agoric Node Observer monitoring works with [node_exporter](https://prometheus.io/docs/guides/node-exporter/) and provides discord messages and emergency calls when a problem occurs.

#### Agoric Node Observer - Features
- Monitoring Memory
- Monitoring Disk
- Monitoring Network condition
- Discord alerts
- Emergency calls

#### Installation
##### 1. Requirement
1. [node_exporter](https://prometheus.io/docs/guides/node-exporter/)
2. [Twillo Vioce API keys & Phone number](https://www.twilio.com/docs/voice/api)
3. [Discord webhook url](https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks)
4. Nodejs v10.23.x or higher
5. PM2

##### 2. Install Agoric-Node-Observer

```
> git clone https://github.com/dsrvlabs/agoric.git

> cd Agoric-Node-Observer && npm install
```

##### 3. Configure

```
# Set all fields in the config file
vi .env
```

##### 4. Start Agoric-Node-Observer with PM2

```
pm2 src/app.js -n agoric-node-observer
```

#### Screenshot
![image](https://user-images.githubusercontent.com/3301716/115503794-f946a600-a2b1-11eb-941e-abdfa3c02a95.png)
![image](https://user-images.githubusercontent.com/3301716/115503834-06639500-a2b2-11eb-91bd-27c620812102.png)
![image](https://user-images.githubusercontent.com/3301716/115503937-24c99080-a2b2-11eb-92ce-367156ce1bf8.png)
![image](https://user-images.githubusercontent.com/3301716/115503956-2c893500-a2b2-11eb-84ca-2696132805e5.png)
![image](https://user-images.githubusercontent.com/3301716/115503973-36129d00-a2b2-11eb-9e08-e6704fc064bc.png)
![image](https://user-images.githubusercontent.com/3301716/115503994-3d39ab00-a2b2-11eb-94a6-9ac80d581a87.png)
