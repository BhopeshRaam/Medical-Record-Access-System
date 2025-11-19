const jwt = require('jsonwebtoken');
const JWT_SECRET = process.env.JWT_SECRET || 'secret';

function auth(req,res,next){
  const header = req.headers.authorization;
  if(!header) return res.status(401).json({ error: 'missing auth' });
  const token = header.split(' ')[1];
  try{
    const payload = jwt.verify(token, JWT_SECRET);
    req.user = payload;
    next();
  }catch(e){
    return res.status(401).json({ error: 'invalid token' });
  }
}

module.exports = auth;
