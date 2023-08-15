FROM node:slim
WORKDIR /valid
COPY . .


RUN npm install \
&& npm run build \
&& rm -rf node_modules \
&& npm install --production
EXPOSE 4000
CMD ["npm","run","start"]
