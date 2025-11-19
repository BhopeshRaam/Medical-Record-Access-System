const mongoose = require('mongoose');

const recordSchema = new mongoose.Schema({
  patientId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  doctorId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  data: Object,
  recordHash: String, // SHA256 of the data
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Record', recordSchema);
