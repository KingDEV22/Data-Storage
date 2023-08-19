"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
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
const express_1 = __importDefault(require("express"));
const mongoose_1 = __importStar(require("mongoose"));
require("dotenv/config");
const amqplib_1 = __importDefault(require("amqplib"));
const joi_1 = __importDefault(require("joi"));
const app = (0, express_1.default)();
const url = "mongodb://0.0.0.0:27017/org";
const PORT = process.env.PORT || 4000;
app.use(express_1.default.json());
const generateForm = (title, body) => {
    const head = `<!DOCTYPE html>
  <html>
  <head>
  <title>${title}</title>
  <style>
  body {
  font-family: Arial, sans-serif;
  text-align: center;
  background-color: #f5f5f5;
  }
  
  form {
  margin: 20px auto;
  width: 300px;
  padding: 20px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 6px 15px rgba(0, 0, 0, 0.1);
  }
  
  label {
  display: block;
  margin-bottom: 5px;
  }
  
  input {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  }
  
  button {
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;
  }
  </style>
  </head>
  <body>
  <form id="${title}">`;
    const formBodyArray = body === null || body === void 0 ? void 0 : body.map((data) => {
        return `<label for="${data.qname}">${data.qlabel}</label>\n<input type="${data.qtype}" id="${data.qname}" name="${data.qname}" /><br /><br />\n`;
    });
    const formBody = formBodyArray === null || formBodyArray === void 0 ? void 0 : formBodyArray.join("");
    let formfoot = `
  <button type="submit">Submit</button>
  </form>
  <script>
  document
        .getElementById("${title}")
        .addEventListener("submit", function (event)
  {
  event.preventDefault();
  
  const formData = new FormData(event.target);
  const questionBody = [];
  formData.forEach((value, key) => {
  questionBody.push({
  name: key,
  value: value
  });
  });
  
  const questions = document.getElementsByTagName("label");
  for (let i = 0; i < questions.length; i++) {
  const questionLabel = questions[i].innerText;
  const questionInput = document.getElementById(
  questions[i].getAttribute("for")
  );
  const questionType = questionInput.type;
  
  questionBody[i]['label'] = questionLabel;
  questionBody[i]['type'] = questionType;
  }
  
  const requestBody = {
  "qa": questionBody,
  "url": window.location.href,
  };
  // Add more questions to requestBody if needed
  console.log(requestBody);
  sendDataToAPI(requestBody);
  });
  
  function sendDataToAPI(data) {
  const apiUrl = "http://localhost:${PORT}/validate"; // Replace with your API endpoint URL
  
  fetch(apiUrl, {
  method: "POST",
  headers: {
  "Content-Type": "application/json",
  },
  body: JSON.stringify(data),
  })
  .then((response) => {
  if (response.ok) {
  console.log("Data sent successfully!");
  } else {
  console.error("Failed to send data.");
  }
  })
  .catch((error) => {
  console.error("An error occurred:", error);
  });
  }
  </script>
  </body>
  </html>
  `;
    return head + formBody + formfoot;
};
const Form = mongoose_1.default.model("form", new mongoose_1.Schema({
    link: String,
    name: String,
    createDate: Date,
    orgId: String,
}, { collection: "form" }));
const Question = mongoose_1.default.model("question", new mongoose_1.Schema({
    qname: String,
    qlabel: String,
    qtype: String,
    fId: String,
}, { collection: "question" }));
const answerSchema = joi_1.default.object({
    name: joi_1.default.string().min(3).required(),
    value: joi_1.default.string().min(1).required(),
    label: joi_1.default.string().min(5).required(),
    type: joi_1.default.string().min(3).required(),
});
mongoose_1.default
    .connect(url)
    .then(() => {
    console.log("connected to database!!");
})
    .catch((error) => {
    console.log(error);
});
app.get("/", (req, res) => {
    res.send("TS App is Running");
});
app.get("/form", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const url = `http://localhost:${PORT}` + req.url.replace("/?", "?");
    console.log(url);
    let data = yield Form.findOne({ link: url }).catch((error) => {
        console.log(error);
        res.send(error);
    });
    let question = yield Question.find({ fId: data === null || data === void 0 ? void 0 : data.id }).catch((error) => {
        console.log(error);
        res.send(error);
    });
    const formData = generateForm(data === null || data === void 0 ? void 0 : data.name, question);
    res.send(formData);
}));
app.post("/validate", (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    var _a;
    const answerData = req.body.qa;
    const url = (_a = req.body.url) === null || _a === void 0 ? void 0 : _a.replace("/?", "?");
    try {
        for (const data of answerData) {
            const { error } = answerSchema.validate(data);
            if (error) {
                return res.status(400).json({ error: error.details[0].message });
            }
        }
        const data = {
            qa: answerData,
            url: url,
        };
        const connection = yield amqplib_1.default.connect("amqp://user:user@rabbitmq:5672");
        const channel = yield connection.createChannel();
        // Makes the queue available to the client
        yield channel.assertQueue("org_db_data");
        //Send a message to the queue
        yield channel.sendToQueue("org_db_data", Buffer.from(JSON.stringify(data)));
        yield channel.close();
        yield connection.close();
        return res.send("Data sent successfully");
    }
    catch (error) {
        console.log(error);
        return res.status(500).json({ error: "Internal server error" });
    }
}));
app.listen(PORT, () => {
    console.log(`server is running on PORT ${PORT}`);
});
