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
require("dotenv/config");
const express_1 = __importDefault(require("express"));
const joi_1 = __importDefault(require("joi"));
const path_1 = __importDefault(require("path"));
const winston_1 = require("winston");
const rabbitConnect_1 = require("./rabbitConnect");
const app = (0, express_1.default)();
const PORT = process.env.PORT || 4000;
//logger initialize
const logger = (0, winston_1.createLogger)({
    format: winston_1.format.combine(winston_1.format.timestamp(), winston_1.format.json()),
    transports: [new winston_1.transports.Console({})],
    exceptionHandlers: [new winston_1.transports.Console({})],
    rejectionHandlers: [new winston_1.transports.Console({})],
});
app.use(express_1.default.json());
app.set("view engine", "ejs");
app.set("views", path_1.default.join(__dirname, "views"));
app.use(express_1.default.urlencoded({ extended: true }));
let questionsData = "";
let url;
const answerSchema = joi_1.default.object({
    name: joi_1.default.string().min(3).required(),
    value: joi_1.default.string().min(1).required(),
    label: joi_1.default.string().min(5).required(),
    type: joi_1.default.string().min(3).required(),
});
(0, rabbitConnect_1.establishConnection)();
app.get("/", (req, res) => {
    res.send("TS App is Running");
});
console.log("Views Directory:", app.get("views"));
app.get("/form", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    url = `http://localhost:${PORT}` + req.url;
    try {
        const data = {
            message: "QUESTIONS",
            url: url,
        };
        (0, rabbitConnect_1.sendToQueue)(data);
        questionsData = yield (0, rabbitConnect_1.recieveFromQueue)();
        const formData = {
            name: questionsData.name,
            data: questionsData.data,
        };
        console.log(formData);
        res.render("form", { formData });
    }
    catch (error) {
        logger.error("", error);
        return res.status(500).json({ error: "Internal server error" });
    }
}));
app.post("/validate", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const answerData = req.body;
    console.log(req.url);
    // const url = (req.body.url as string)?.replace("/?", "?");
    try {
        logger.info("Data validated!!!");
        const data = {
            formData: answerData,
            url: url,
            message: "SUBMIT",
        };
        (0, rabbitConnect_1.sendToQueue)(data);
        logger.info("Data sent to message queue");
        return res.send("Data sent successfully");
    }
    catch (error) {
        logger.error("", error);
        return res.status(500).json({ error: "Internal server error" });
    }
}));
app.listen(PORT, () => {
    console.log(`server is running on PORT ${PORT}`);
});
