const express = require('express');
const router = express.Router();
const User = require('../models/User');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'secret';

router.post('/register', async (req,res)=>{
  try{
    const { name, email, password, role } = req.body;
    if(!email || !password) return res.status(400).json({ error: 'email and password required' });
    const existing = await User.findOne({ email });
    if(existing) return res.status(400).json({ error: 'user exists' });
    const hash = await bcrypt.hash(password, 10);
    const user = new User({ name, email, passwordHash: hash, role });
    await user.save();
    const token = jwt.sign({ id: user._id, role: user.role }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ token, user: { id: user._id, name: user.name, email: user.email, role: user.role } });
  }catch(e){
    console.error(e);
    res.status(500).json({ error: 'server error' });
  }
});

router.post('/login', async (req,res)=>{
  try{
    const { email, password } = req.body;
    const user = await User.findOne({ email });
    if(!user) return res.status(400).json({ error: 'invalid credentials' });
    const ok = await bcrypt.compare(password, user.passwordHash);
    if(!ok) return res.status(400).json({ error: 'invalid credentials' });
    const token = jwt.sign({ id: user._id, role: user.role }, JWT_SECRET, { expiresIn: '7d' });
    res.json({ token, user: { id: user._id, name: user.name, email: user.email, role: user.role } });
  }catch(e){
    console.error(e);
    res.status(500).json({ error: 'server error' });
  }
});

module.exports = router;
