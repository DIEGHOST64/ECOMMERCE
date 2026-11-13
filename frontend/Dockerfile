FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund

COPY . .

ARG REACT_APP_AUTH_URL=http://localhost:9090
ARG REACT_APP_CATALOGO_URL=http://localhost:9092
ARG REACT_APP_REQUEST_TIMEOUT_MS=15000
ENV REACT_APP_AUTH_URL=$REACT_APP_AUTH_URL
ENV REACT_APP_CATALOGO_URL=$REACT_APP_CATALOGO_URL
ENV REACT_APP_REQUEST_TIMEOUT_MS=$REACT_APP_REQUEST_TIMEOUT_MS

RUN npm run build

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -q --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
