FROM node:16-alpine
RUN mkdir /opt/app
COPY ./ /opt/app/
WORKDIR /opt/app/
RUN rm -rf node_modules
RUN npm install
CMD ["npm", "start"]