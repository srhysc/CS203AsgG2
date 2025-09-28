# frontend/Dockerfile
# ---------- common base ----------
FROM node:20 AS base
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .

# ---------- DEV image (Vite on 5173) ----------
FROM base AS dev
EXPOSE 5173
CMD ["npm","run","dev","--","--host","0.0.0.0","--port","5173"]

# ---------- PROD build ---------- (fix later lol)
FROM base AS build
# bake API base only for prod builds (or use .env.production)
ARG VITE_API_BASE=http://localhost:8080
ENV VITE_API_BASE=${VITE_API_BASE}
RUN npm run build

# ---------- PROD runtime image (static files on 3000) ----------
FROM node:20 AS prod
WORKDIR /app
RUN npm i -g serve
COPY --from=build /app/dist ./dist
EXPOSE 3000
CMD ["serve","-s","dist","-l","3000"]
