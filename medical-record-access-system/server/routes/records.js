const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const Record = require('../models/Record');
const User = require('../models/User');
const SimpleChain = require('../blockchain/blockchain');
const crypto = require('crypto');

const chain = new SimpleChain(process.env.BLOCKCHAIN_FILE || './chain.json');

// Create a new medical record (doctor or patient)
router.post('/', auth, async (req,res)=>{
  try{
    const { patientId, data } = req.body;
    if(!patientId || !data) return res.status(400).json({ error: 'patientId and data required' });
    // compute SHA256 of the data for blockchain anchoring
    const hash = crypto.createHash('sha256').update(JSON.stringify(data)).digest('hex');
    const record = new Record({
      patientId, doctorId: req.user.id, data, recordHash: hash
    });
    await record.save();
    // add to chain (only storing the hash and meta)
    const block = chain.addBlock({ type: 'record', recordId: record._id.toString(), recordHash: hash, addedBy: req.user.id });
    res.json({ record, block });
  }catch(e){
    console.error(e);
    res.status(500).json({ error: 'server error' });
  }
});

// Get records for a patient (doctor must be authorized — for demo we allow doctor/patient)
router.get('/patient/:patientId', auth, async (req,res)=>{
  try{
    const { patientId } = req.params;
    // demo: allow if requester is that patient or a doctor/admin
    if(req.user.id !== patientId && req.user.role === 'patient'){
      return res.status(403).json({ error: 'forbidden' });
    }
    const records = await Record.find({ patientId }).sort({ createdAt: -1 });
    res.json({ records });
  }catch(e){
    console.error(e);
    res.status(500).json({ error: 'server error' });
  }
});

router.get('/chain', auth, async (req,res)=>{
  try{
    const c = chain.getChain();
    res.json({ chain: c });
  }catch(e){
    console.error(e);
    res.status(500).json({ error: 'server error' });
  }
});

module.exports = router;
