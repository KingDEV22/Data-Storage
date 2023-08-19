import express, { Application, Request, Response } from "express";
import mongoose, { Schema } from "mongoose";
import "dotenv/config";
import client, { Channel, Connection } from "amqplib";
import Joi from "joi";
import axios from "axios";
const app: Application = express();
const url = "mongodb://localhost:27017/org";
const PORT = process.env.PORT || 4000;
app.use(express.json());
const generateForm = (title?: string, body?: any): String => {
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

  const formBodyArray = body?.map((data: any) => {
    return `<label for="${data.qname}">${data.qlabel}</label>\n<input type="${data.qtype}" id="${data.qname}" name="${data.qname}" /><br /><br />\n`;
  });

  const formBody = formBodyArray?.join("");

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

const Form = mongoose.model(
  "form",
  new Schema(
    {
      link: String,
      name: String,
      createDate: Date,
      orgId: String,
    },
    { collection: "form" }
  )
);

const Question = mongoose.model(
  "question",
  new Schema(
    {
      qname: String,
      qlabel: String,
      qtype: String,
      fId: String,
    },
    { collection: "question" }
  )
);

interface answerData {
  name: String;
  value: String;
  type: String;
  label: String;
}

const answerSchema = Joi.object({
  name: Joi.string().min(3).required(),
  value: Joi.string().min(1).required(),
  label: Joi.string().min(5).required(),
  type: Joi.string().min(3).required(),
});

mongoose
  .connect(url)
  .then(() => {
    console.log("connected to database!!");
  })
  .catch((error: any) => {
    console.log(error);
  });

app.get("/", (req: Request, res: Response) => {
  res.send("TS App is Running");
});

app.get("/form", async (req: Request, res: Response) => {
  const url = `http://localhost:${PORT}` + req.url.replace("/?", "?");
  console.log(url);
  let data = await Form.findOne({ link: url }).catch((error: any) => {
    console.log(error);
    res.send(error);
  });
  let question = await Question.find({ fId: data?.id }).catch((error: any) => {
    console.log(error);
    res.send(error);
  });
  const formData = generateForm(data?.name, question);

  res.send(formData);
});

app.post("/validate", async (req: Request, res: Response) => {
  const answerData: answerData[] = req.body.qa;
  const url = (req.body.url as string)?.replace("/?", "?");

  try {
    for (const data of answerData) {
      const { error } = answerSchema.validate(data);
      if (error) {
        return res.status(400).json({ error: error.details[0].message });
      }
    }

    const data = {
      qa:answerData,
      url:url,
    }

    const connection: Connection = await client.connect(
      "amqp://user:user@rabbitmq:5672"
    );

    const channel: Channel = await connection.createChannel();
    // Makes the queue available to the client
    await channel.assertQueue("org_db_data");

    //Send a message to the queue
    await channel.sendToQueue(
      "org_db_data",
      Buffer.from(JSON.stringify(data))
    );
    await channel.close();
    await connection.close();

    return res.send("Data sent successfully");
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: "Internal server error" });
  }
});

app.listen(PORT, () => {
  console.log(`server is running on PORT ${PORT}`);
});
