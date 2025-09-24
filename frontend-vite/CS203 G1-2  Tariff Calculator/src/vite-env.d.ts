// File for environment variable types
// (e.g. API URL strings and titles)

//tells typescript these variables exist so no error.
interface ImportMetaEnv {
  readonly VITE_API_URL: string
  readonly VITE_CLERK_PUBLISHABLE_KEY: string
}

//connects .env variables to import.meta.objects (above)
interface ImportMeta {
  readonly env: ImportMetaEnv
}
