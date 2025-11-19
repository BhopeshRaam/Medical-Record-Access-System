# Medical Record Access System (MRAS) 

This repository contains a demo MERN application that shows how you might anchor medical record hashes into a simple file-based blockchain while storing full record data in MongoDB.

**Important:** This is a teaching/demo project. It is NOT production ready. Do not use with real patient data without proper security, encryption, auditing, and legal compliance (HIPAA/GDPR/etc).

Structure:

- server/ - Express backend with MongoDB and a tiny blockchain module
- client/ - React client (very minimal)

To run locally:

1. Install MongoDB and run it.
2. In `server/`: copy `.env.example` to `.env`, set variables, then `npm install` and `npm run dev`.
3. In `client/`: `npm install` then `REACT_APP_API=http://localhost:5000 npm start`.

What the blockchain does:

- Stores blocks containing `recordHash` (SHA256 of the JSON medical record) and metadata.
- Demonstrates immutability by chaining hashes; the implementation is file-based and for learning only.

If you want a production-grade solution:

- Use a permissioned blockchain (Hyperledger Fabric, Quorum, or an audited private Ethereum network).
- Store only hashes on-chain and the data encrypted in a secure data store (or use IPFS + encryption).
- Implement access control, audit logs stored immutably, and consult legal/compliance experts.
