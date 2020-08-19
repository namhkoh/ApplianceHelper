import express from 'express';
import bodyparser from 'body-parser';
import https from 'https';
import  fs from 'fs';
import winston from 'winston';
import expressWinston from 'express-winston';
import cors from 'cors';
import mongoose from 'mongoose';
import WebSocket from 'ws';

// Local Modules
import config from './src/utils/config';
import mainRouter from './src/routes/MainRoute';
import { startPushMessageServer } from './src/device/pushMessage';
import { initLoadData } from './src/data/localDataFetch';

const app = express();
const corsOptions = {
    origin: '*',
    optionsSuccessStatus: 200
};

app.use(cors(corsOptions));
// Body parser
app.use(bodyparser.urlencoded({ extended: true }));
app.use(bodyparser.json());
// initialize our logger (in our case, winston + papertrail)
/*app.use(
    expressWinston.logger({
            transports: [
                    new winston.transports.Papertrail({
                            host: config.Logger.Host,
                            port: config.Logger.Port,
                            level: 'error',
                    }),
            ],
            meta: true,
    })
);*/

// Mongoose : mongodb connection for dbpyxz
/*mongoose.Promise=global.Promise;
mongoose.set('useCreateIndex', true);
mongoose.connect('mongodb://localhost/dbbvqa',
    { useNewUrlParser: true }
);
var chalk = require('chalk');
var connected = chalk.bold.cyan;
var error = chalk.bold.yellow;
var disconnected = chalk.bold.red;
var termination = chalk.bold.magenta;

    mongoose.connection.on('connected', function(){
        console.log(connected("Mongo Db connection open to ", 
        mongoose.connection.host+ ":" +mongoose.connection.name));
        //initLoadData();
    });

    mongoose.connection.on('error', function(err){
        console.log(error("Mongoose default connection has occured "+err+" error"));
    });

    mongoose.connection.on('disconnected', function(){
        console.log(disconnected("Mongoose default connection is disconnected"));
    });
*/
//
var ip = require('ip');
console.log("### MULTI DOMAIN QA SYSTEM ###");
console.log("[PYXZ:QA] @ " + ip.address() + " : " + config.Server.PORT);
//mlogger.info("What we've got here is...failure to communicate");
mainRouter(app);
//PORT for listening various http messages
var server = app.listen(config.Server.PORT, (err, port) => {
    if (err)
        process.exit(1);
});

const wss = new WebSocket.Server({ noServer: true },()=>{
    console.log("server started websockets");
});

wss.on('connection', function connection(ws) {
  ws.on('message', function incoming(message) {
    console.log('received: %s', message);
  });

  ws.send('something');
});
//PORT for push notifications form server to device
startPushMessageServer(server);
