# Use an official Node.js runtime as a parent image (Assuming backend is Node.js based on Render usage)
FROM node:18-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy package files and install dependencies
# Note: You need to place your backend source code in a 'backend' folder in the project root
COPY backend/package*.json ./
RUN npm install --production

# Copy the rest of your backend source code
COPY backend/ .

# Expose the port your app runs on
EXPOSE 3000

# Define environment variables
ENV NODE_ENV=production
ENV PORT=3000

# Start the application
CMD [ "node", "index.js" ]
