import client, { Channel, Connection, ConsumeMessage } from "amqplib";
import { createLogger, format, transports } from "winston";

const logger = createLogger({
  format: format.combine(format.timestamp(), format.json()),
  transports: [new transports.Console({})],
  exceptionHandlers: [new transports.Console({})],
  rejectionHandlers: [new transports.Console({})],
});

const rabbitmqUrl = "amqp://guest:guest@localhost:5672";
let connection: Connection | null = null;

export const establishConnection = async () => {
  if (connection === null) {
    connection = await client.connect(rabbitmqUrl);
    logger.info("Connected to rabbitmq");
  }
};

export const sendToQueue = async (data: Object) => {
  if (connection) {
    const channel: Channel = await connection.createChannel();
    // Makes the queue available to the client
    await channel.assertQueue("data_consume");

    //Send a message to the queue
    await channel.sendToQueue(
      "data_consume",
      Buffer.from(JSON.stringify(data))
    );

    await channel.close();
  }
};

export const recieveFromQueue = async () => {
  if (connection) {
    const channel: Channel = await connection.createChannel();
    // Makes the queue available to the client
    await channel.assertQueue("data_produce");
    let data = {};
    //Send a message to the queue
    await channel.consume("data_produce", (message: ConsumeMessage | null) => {
      if (message) {
        data = JSON.parse(message.content.toString());
        // console.log(message?.content.toString());
        channel.ack(message);
      }
    });
    await channel.close();
    return data;
  }
};

export const closeConnection = async() => {
    await connection?.close();
}
