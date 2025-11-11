1) Ensure docker is downloaded 

run these commands from the main folder "CS203ASGG2"
docker compose --profile dev up -d
docker compose ps


if you want to stop and shutdown everything run 

docker compose down


alternatively open the docker app and stop it   

## CI/CD (GitHub Actions)

- Workflow location: `.github/workflows/ci.yml`. It runs on pushes and pull requests targeting `main`, and it can be triggered manually from the Actions tab via **Run workflow**.
- `Backend · Maven Verify`: spins up Temurin JDK 21, executes `./mvnw verify`, and uploads the packaged Spring Boot jar plus the JaCoCo coverage report as the `backend-package` artifact.
- `Frontend · Lint & Build`: uses Node 20, runs `npm ci`, `npm run lint`, and `npm run build` inside `frontend-vite/TariffCalculator`, then publishes the Vite `dist/` output as the `frontend-dist` artifact.
- `Docker images · Compose build`: depends on both build jobs and ensures `docker compose` can build the backend, dev frontend, and prod frontend images that power the `docker-compose.yml` stack.
- All artifacts are downloadable directly from the workflow run page, which lets you promote the build output to any environment (e.g., upload the frontend bundle to a static host or deploy the backend jar to your chosen runtime).
