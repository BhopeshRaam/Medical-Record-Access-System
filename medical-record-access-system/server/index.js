require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mongoose = require('mongoose');
const authRoutes = require('./routes/auth');
const recordRoutes = require('./routes/records');
const app = express();

app.use(cors());
app.use(bodyParser.json());

app.use('/api/auth', authRoutes);
app.use('/api/records', recordRoutes);

const PORT = process.env.PORT || 5000;
const MONGO = process.env.MONGO_URI || 'mongodb://localhost:27017/mras';

mongoose.connect(MONGO, { useNewUrlParser: true, useUnifiedTopology: true })
.then(()=> {
  console.log('MongoDB connected');
  app.listen(PORT, ()=> console.log('Server running on', PORT));
})
.catch(err=> console.error('MongoDB error', err));
