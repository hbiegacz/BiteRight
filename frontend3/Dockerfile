# =========== BUILD ==============
FROM node:20-alpine AS build-stage

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Set environment variables for build
ENV NEXT_PUBLIC_API_URL=http://localhost:8080
RUN npm run build

# =========== CREATE A SERVER =============
FROM nginx:1.27.2 AS server-stage

# copy server configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# copy built files to server files
# Next.js static export goes to 'out' directory
COPY --from=build-stage /app/out /usr/share/nginx/html

# Change ownership of relevant directories to nginx user
RUN chown -R nginx:nginx /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx/conf.d

# Give permissions to /var/run and other relevant directories
RUN chmod -R 755 /var/cache/nginx /var/log/nginx /usr/share/nginx/html /etc/nginx/conf.d /var/run

EXPOSE 80

# Command to start NGINX when the container is run
CMD ["nginx", "-g", "daemon off;"]
