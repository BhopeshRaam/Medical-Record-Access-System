const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  name: String,
  email: { type: String, unique: true },
  passwordHash: String,
  role: { type: String, enum: ['doctor','patient','admin'], default: 'patient' }
},{ timestamps: true });

module.exports = mongoose.model('User', userSchema);
