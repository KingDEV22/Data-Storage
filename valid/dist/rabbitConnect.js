"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.closeConnection = exports.recieveFromQueue = exports.sendToQueue = exports.establishConnection = void 0;
const amqplib_1 = __importDefault(require("amqplib"));
const winston_1 = require("winston");
const logger = (0, winston_1.createLogger)({
    format: winston_1.format.combine(winston_1.format.timestamp(), winston_1.format.json()),
    transports: [new winston_1.transports.Console({})],
    exceptionHandlers: [new winston_1.transports.Console({})],
    rejectionHandlers: [new winston_1.transports.Console({})],
});
const rabbitmqUrl = "amqp://guest:guest@localhost:5672";
let connection = null;
const establishConnection = () => __awaiter(void 0, void 0, void 0, function* () {
    if (connection === null) {
        connection = yield amqplib_1.default.connect(rabbitmqUrl);
        logger.info("Connected to rabbitmq");
    }
});
exports.establishConnection = establishConnection;
const sendToQueue = (data) => __awaiter(void 0, void 0, void 0, function* () {
    if (connection) {
        const channel = yield connection.createChannel();
        // Makes the queue available to the client
        yield channel.assertQueue("data_consume");
        //Send a message to the queue
        yield channel.sendToQueue("data_consume", Buffer.from(JSON.stringify(data)));
        yield channel.close();
    }
});
exports.sendToQueue = sendToQueue;
const recieveFromQueue = () => __awaiter(void 0, void 0, void 0, function* () {
    if (connection) {
        const channel = yield connection.createChannel();
        // Makes the queue available to the client
        yield channel.assertQueue("data_produce");
        let data = {};
        //Send a message to the queue
        yield channel.consume("data_produce", (message) => {
            if (message) {
                data = JSON.parse(message.content.toString());
                // console.log(message?.content.toString());
                channel.ack(message);
            }
        });
        yield channel.close();
        return data;
    }
});
exports.recieveFromQueue = recieveFromQueue;
const closeConnection = () => __awaiter(void 0, void 0, void 0, function* () {
    yield (connection === null || connection === void 0 ? void 0 : connection.close());
});
exports.closeConnection = closeConnection;
