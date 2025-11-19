# MRAS Server (Medical Record Access System) - Backend

This is a demo backend for the MRAS project.

Important notes:

- This demo uses a simple file-based 'blockchain' to anchor SHA256 hashes of medical records.
- **Do not use this in production with real PHI** unless you ensure compliance with regional healthcare privacy laws (HIPAA, GDPR, etc).
- The chain is stored in `chain.json` and is append-only for the demo.

Setup:

1. Install MongoDB and run locally, or provide MONGO_URI in .env
2. cd server
3. cp .env.example .env and edit
4. npm install
5. npm run dev
