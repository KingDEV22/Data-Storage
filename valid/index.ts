import client, { Channel, Connection, ConsumeMessage } from "amqplib";
import "dotenv/config";
import express, { Application, Request, Response } from "express";
import Joi from "joi";
import path from "path";
import { createLogger, format, transports } from "winston";
import { establishConnection, sendToQueue, recieveFromQueue } from "./rabbitConnect";
const app: Application = express();
const PORT = process.env.PORT || 4000;
//logger initialize
const logger = createLogger({
  format: format.combine(format.timestamp(), format.json()),
  transports: [new transports.Console({})],
  exceptionHandlers: [new transports.Console({})],
  rejectionHandlers: [new transports.Console({})],
});

app.use(express.json());
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));
app.use(express.urlencoded({ extended: true }));

let questionsData: any = "";
let url: String;
establishConnection();



app.get("/", (req: Request, res: Response) => {
  res.send("TS App is Running");
});
console.log("Views Directory:", app.get("views"));

app.get("/form", async (req: Request, res: Response) => {
  url = `http://localhost:${PORT}` + req.url;
  try {
    const data = {
      message: "QUESTIONS",
      url: url,
    };
   
    sendToQueue(data);
    logger.info("Request for form data sent")
    questionsData = await recieveFromQueue();
    const formData = {
      name: questionsData.name,
      data: questionsData.data,
    };
    logger.info("form Data recieved.")
    res.render("form", { formData });
  } catch (error) {
    logger.error("", error);
    return res.status(500).json({ error: "Internal server error" });
  }
});

app.post("/validate", async (req: Request, res: Response) => {
  try {
    const answerData: Object = req.body;
    logger.info("Data validated!!!");
    const data = {
      formData: answerData,
      url: url,
      message: "SUBMIT",
    };
    sendToQueue(data);
    logger.info("Data sent to message queue");
    return res.send("Data sent successfully");
  } catch (error) {
    logger.error("", error);
    return res.status(500).json({ error: "Internal server error" });
  }
});

app.listen(PORT, () => {
  console.log(`server is running on PORT ${PORT}`);
});
