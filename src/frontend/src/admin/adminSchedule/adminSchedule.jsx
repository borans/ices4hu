import SideBar from "../sidebarAdmin/sidebarAdmin";
import React, { useState, useEffect } from "react";
import styles from "../../components/courses.module.css";
import TopBar from "../topbarAdmin/topbarAdmin";


function AdminSchedule() {

  const [scheduleData, setSchedule] = useState([]);
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [startDate, setStartDate] = useState();
  const [deadline, setDeadline] = useState();
  const [gradeFinalizationDate, setGradeFinalizationDate] = useState();

  let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;

  useEffect(() => {
    fetchSchedule()
  }, [])


  const fetchSchedule = () => {
    fetch("/api/schedule", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
    })
      .then((res) => {
        if (res.status === 404)
          throw new Error("NO SCHEDULE");
        else if (res.ok)
          return res.json();
        else
          throw new Error("AN ISSUE CONNECTING TO BACKEND");
      }
      )
      .then(result => {
        setSchedule(result);
        setStartDate(result.startDate);
        setGradeFinalizationDate(result.gradeFinalizationDate);
        setDeadline(result.endDate);
        setIsLoaded(true);
      })
      .catch((err) => {
        console.log(err.message)
        setIsLoaded(true);
        if (err.message !== "NO SCHEDULE") {
          setError(err);
          console.log("Error:", err);
        }
        if (err.message === "NO SCHEDULE") {
          setSchedule(JSON.stringify({
            startDate: null,
            gradeFinalizationDate: null,
            endDate: null
          }));
        }
      });
  };

  const updateSchedule = () => {
    if (startDate==="" || startDate === undefined){
        alert("Please fill the Starting Time date.")
        return;
    }
    if (gradeFinalizationDate==="" || gradeFinalizationDate === undefined){
      alert("Please fill Grade Finalization Date date.") 
      return;
  }
  if (deadline==="" || deadline === undefined){
    alert("Please fill the Deadline Date date.") 
    return;
}

if(scheduleData !== null && !(scheduleData.gradeFinalizationDate === null || scheduleData.startDate !== null || scheduleData.endDate !== null)){
  alert("Schedule Is Already Set.")
  return;
}

    if((new Date(startDate) >= new Date(deadline))
        ||
      (new Date(deadline) >= new Date(gradeFinalizationDate))
      ||
      (new Date(startDate) >= new Date(gradeFinalizationDate))
    ){
      alert("Invalid dates, please check again.") 
      return;
    }


    fetch("/api/schedule/admin", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
      body: JSON.stringify({
        startDate: startDate,
        endDate: deadline,
        gradeFinalizationDate: gradeFinalizationDate,
      }),
    })
    .then((res) => {
      if (res.ok) {
          return {json: res.json(),
              success: true};
      }

      else {
          return {json: res.json(),
              success: false};
      }
    })
    .then((result) => {

      if (result.success) {
        result.json.then((json) => {
          alert(json.message);
      })
      } else {
          result.json.then((json) => {
              alert(json.message);
          })
      }
    })
      .catch((err) => {
        console.log("Error:", err);
        alert("COULD NOT SAVE SCHEDULE")
      });
  };

  if (HACKINGTRY) {
    return <div> ACCESS DENIED </div>;
  }
  else if (error) {
    return <div> Error !!!</div>;
  } else if (!isLoaded) {
    return <div> Loading... </div>;
  } else {
    return (
      <div>
        <TopBar />
        <div className={styles.page}>
          <div>
            <SideBar />
          </div>

          <div className="container">

            <h1 className="page-title">Schedule</h1>

            <div name="tableCourse" >

              <table className={styles.table}>

                <thead className={styles.th}>

                  <tr className={styles.tr}>
                    <th>Starting Time</th>
                    <th>End Date</th>
                    <th>Grade Finalization Date</th>
                  </tr>
                </thead> 
                  <tbody className={styles.td}>
                    <tr>
                      <td>{                  <input
                    type="datetime-local"
                    defaultValue={scheduleData.startDate}
                    value={startDate}
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setStartDate(e.target.value)}
                  />}</td>
                      <td>{                  
                      <input
                    type="datetime-local"
                    defaultValue={scheduleData.endDate}
                    value={deadline}
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setDeadline(e.target.value)}
                  />}</td>
                      <td>{                  
                      <input
                    type="datetime-local"
                    defaultValue={scheduleData.gradeFinalizationDate}
                    value={gradeFinalizationDate}
                    min={new Date().toISOString().slice(0, -8)}
                    onChange={(e) => setGradeFinalizationDate(e.target.value)}
                  />}</td>
                    </tr>
                  </tbody>
              </table>
            </div>
          </div>
              <div>
              <button type="submit" className="action-button" onClick={(event) => {event.preventDefault(); updateSchedule();}}>Set Academic Calendar</button>
                </div>  
        </div>
      </div>
    );
  }
}
export default AdminSchedule;