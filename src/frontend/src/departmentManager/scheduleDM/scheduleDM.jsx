import React, {useState, useEffect} from "react";
import styles from "../../components/courses.module.css";
import ScheduleTable_DM from "../scheduleStudentTable/scheduleDMTable";
import SideBar from "../sidebarManager/sidebarManager";
import TopBar from "../topbarManager/topbarManager";

function DMSchedule() {

    const [eventList, setEventList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);


    let HACKINGTRY = localStorage.getItem("loginID") == null ? true : false;


    useEffect(() => {
        fetchEvents()
    }, [])

  const fetchEvents = () => {
    fetch("/api/schedule", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": localStorage.getItem("authToken")
      },
    })
      .then((res) => {
        if(res.status===404)
        throw new Error("NO SCHEDULE");
        else if (res.ok)
          return res.json();
        else
          throw new Error("AN ISSUE CONNECTING TO BACKEND");
      }
      )
      .then(result => {
        setEventList(result);
        setIsLoaded(true);
      })
      .catch((err) => {
        console.log(err.message)
        setIsLoaded(true);
        if(err.message !== "NO SCHEDULE"){
        setError(err);
        console.log("Error:", err);
       }
       if(err.message === "NO SCHEDULE"){
        setEventList(null);
       }
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
                        </thead> {eventList !== null &&
                        <tbody className={styles.td}>

                            <ScheduleTable_DM key={eventList.id}
                                startingTime = {eventList.startDate}
                                endDate = {eventList.endDate}
                                gradeFinalizationDate = {eventList.gradeFinalizationDate} />

                        </tbody>}
                        {eventList === null &&
                                      <div>
                                          <label> ACADEMIC SCHEDULE HAS NOT BEEN RELEASED YET</label>
                                      </div>
                                    }
                    </table>

                </div>
            </div>

        </div>
        </div>
    );
    }

}

export default DMSchedule;