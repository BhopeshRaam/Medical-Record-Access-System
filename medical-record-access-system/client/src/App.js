import React, {useState} from 'react';

const API = process.env.REACT_APP_API || 'http://localhost:5000/api';

function App(){
  const [email,setEmail] = useState('');
  const [password,setPassword] = useState('');
  const [token,setToken] = useState(localStorage.getItem('token')||'');
  const [patientId,setPatientId] = useState('');
  const [recordData,setRecordData] = useState('{}');
  const [records,setRecords] = useState([]);
  const [chain,setChain] = useState([]);

  async function register(){
    const res = await fetch(API + '/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({name:'Demo',email,password,role:'doctor'})});
    const j = await res.json();
    if(j.token){ setToken(j.token); localStorage.setItem('token', j.token); alert('registered'); }
    else alert(JSON.stringify(j));
  }

  async function login(){
    const res = await fetch(API + '/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email,password})});
    const j = await res.json();
    if(j.token){ setToken(j.token); localStorage.setItem('token', j.token); alert('logged in'); }
    else alert(JSON.stringify(j));
  }

  async function addRecord(){
    try{
      const data = JSON.parse(recordData);
      const res = await fetch(API + '/records', { method:'POST', headers: {'Content-Type':'application/json', 'Authorization':'Bearer '+token}, body: JSON.stringify({ patientId, data })});
      const j = await res.json();
      if(res.ok){ alert('record added'); }
      else alert(JSON.stringify(j));
    }catch(e){ alert('invalid json'); }
  }

  async function fetchRecords(){
    const res = await fetch(API + '/records/patient/' + patientId, { headers: { 'Authorization': 'Bearer ' + token } });
    const j = await res.json();
    if(res.ok) setRecords(j.records);
    else alert(JSON.stringify(j));
  }

  async function fetchChain(){
    const res = await fetch(API + '/records/chain', { headers: { 'Authorization': 'Bearer ' + token } });
    const j = await res.json();
    if(res.ok) setChain(j.chain);
    else alert(JSON.stringify(j));
  }

  return (
    <div style={{ padding:20, fontFamily:'Arial' }}>
      <h2>Medical Record Access System (Demo)</h2>
      <section style={{ marginBottom:20 }}>
        <h3>Auth</h3>
        <input placeholder="email" value={email} onChange={e=>setEmail(e.target.value)} />{' '}
        <input placeholder="password" value={password} onChange={e=>setPassword(e.target.value)} type="password" />{' '}
        <button onClick={register}>Register (demo doctor)</button>{' '}
        <button onClick={login}>Login</button>
      </section>

      <section style={{ marginBottom:20 }}>
        <h3>Create record</h3>
        <input placeholder="patientId" value={patientId} onChange={e=>setPatientId(e.target.value)} style={{ width:300 }} /><br/>
        <textarea rows={6} cols={80} value={recordData} onChange={e=>setRecordData(e.target.value)} />
        <br/>
        <button onClick={addRecord}>Add Record</button>
      </section>

      <section style={{ marginBottom:20 }}>
        <h3>View records</h3>
        <button onClick={fetchRecords}>Fetch Records for Patient</button>{' '}
        <button onClick={fetchChain}>Fetch Chain</button>
        <div>
          <h4>Records</h4>
          <pre>{JSON.stringify(records, null, 2)}</pre>
        </div>
        <div>
          <h4>Blockchain</h4>
          <pre>{JSON.stringify(chain, null, 2)}</pre>
        </div>
      </section>
    </div>
  );
}

export default App;
