/*
A tiny, file-based blockchain used to store record hashes and access logs.
Not a production-grade blockchain — it's a teaching/demo component that
provides append-only immutable blocks saved to a JSON file.
*/
const fs = require('fs');
const crypto = require('crypto');
const path = require('path');

class SimpleChain {
  constructor(filePath){
    this.filePath = filePath || path.join(__dirname,'../chain.json');
    if(!fs.existsSync(this.filePath)){
      const genesis = [{
        index: 0,
        timestamp: Date.now(),
        data: { info: 'genesis' },
        prevHash: '0',
        hash: '0'
      }];
      fs.writeFileSync(this.filePath, JSON.stringify(genesis, null, 2));
    }
  }

  _read(){
    return JSON.parse(fs.readFileSync(this.filePath, 'utf8'));
  }

  _write(chain){
    fs.writeFileSync(this.filePath, JSON.stringify(chain, null, 2));
  }

  _hashBlock(index, timestamp, data, prevHash){
    return crypto.createHash('sha256').update(index+timestamp+JSON.stringify(data)+prevHash).digest('hex');
  }

  addBlock(data){
    const chain = this._read();
    const last = chain[chain.length-1];
    const index = last.index + 1;
    const timestamp = Date.now();
    const prevHash = last.hash;
    const hash = this._hashBlock(index, timestamp, data, prevHash);
    const block = { index, timestamp, data, prevHash, hash };
    chain.push(block);
    this._write(chain);
    return block;
  }

  getChain(){
    return this._read();
  }
}

module.exports = SimpleChain;
