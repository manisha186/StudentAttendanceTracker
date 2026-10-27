
import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';

const API = 'http://localhost:8080/api';

function App() {
  const [students, setStudents] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [name, setName] = useState('');
  const [rollNumber, setRollNumber] = useState('');
  const [date, setDate] = useState(
    new Date().toLocaleDateString('en-CA')
  );
  const [error, setError] = useState('');

  const loadStudents = async () => {
    try {
      const res = await axios.get(`${API}/students`);
      setStudents(res.data);
    } catch {
      setError('Cannot connect to backend.');
    }
  };

  const loadAttendance = async () => {
    try {
      const res = await axios.get(
        `${API}/attendance?date=${date}`
      );
      setAttendance(res.data);
      setError('');
    } catch (err) {
  console.error('Attendance error:', err);
  setError(
    err.response?.data?.message ||
    err.message ||
    'Failed to load attendance.'
  );
}
  };

  useEffect(() => {
    loadStudents();
  }, []);

  useEffect(() => {
    loadAttendance();
  }, [date]);

  const addStudent = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`${API}/students`, {
        name,
        rollNumber
      });
      setName('');
      setRollNumber('');
      setError('');
      loadStudents();
    } catch (err) {
      setError(err.response?.data?.error || 'Could not add student.');
    }
  };

  const markAttendance = async (studentId, status) => {
    try {
      await axios.post(`${API}/attendance`, {
        studentId: String(studentId),
        date,
        status
      });
      await loadAttendance();
    } catch {
      setError('Could not save attendance.');
    }
  };

  const getRecord = (studentId) =>
    attendance.find((a) => a.student.id === studentId);

  const present = attendance.filter(
    (a) => a.status === 'Present'
  ).length;

  const absent = attendance.filter(
    (a) => a.status === 'Absent'
  ).length;

  return (
    <main className="container">
      <h1>Student Attendance Tracker</h1>
      <p className="subtitle">Manage daily class attendance</p>

      <form className="student-form" onSubmit={addStudent}>
        <input
          placeholder="Student name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          placeholder="Roll number"
          value={rollNumber}
          onChange={(e) => setRollNumber(e.target.value)}
          required
        />
        <button type="submit">Add Student</button>
      </form>

      <section className="date-section">
        <label>
          Attendance date:
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
        </label>
      </section>

      <section className="summary">
        <div>Total Students: {students.length}</div>
        <div>Present: {present}</div>
        <div>Absent: {absent}</div>
        <div>Not Marked: {students.length - attendance.length}</div>
      </section>

      {error && <p className="error">{error}</p>}

      <table>
        <thead>
          <tr>
            <th>Roll Number</th>
            <th>Name</th>
            <th>Status</th>
            <th>Mark Attendance</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student) => {
            const record = getRecord(student.id);
            return (
              <tr key={student.id}>
                <td>{student.rollNumber}</td>
                <td>{student.name}</td>
                <td>{record?.status || 'Not marked'}</td>
                <td>
                  <button
                    onClick={() =>
                      markAttendance(student.id, 'Present')
                    }
                  >
                    Present
                  </button>
                  {' '}
                  <button
                    onClick={() =>
                      markAttendance(student.id, 'Absent')
                    }
                  >
                    Absent
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </main>
  );
}

export default App;
