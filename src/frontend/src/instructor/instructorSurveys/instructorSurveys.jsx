import { useNavigate } from 'react-router-dom';
import SideBar from "../sidebarInstructor/sidebarInstructor";
import React, { useState, useEffect } from "react";
import SurveyTable_Instructor from "../surveyTableInstructor/surveyInstructor";
import styles from "../../components/surveys.module.css";
import TopBar from "../topbarInstructor/topbarInstructor";
import my_surveys from "../../assets/my_surveys.png";
import folder_logo from "../../assets/folder_logo.png";

function InstructorSurveys() {
    let navigate = useNavigate();
    const [surveyList, setSurveyList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [eventList, setEventList] = useState([]);
    let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;


    useEffect(() => {
        fetchSurveys()
        fetchEvents()
    }, [])

    function removeSurvey(index) {
        setSurveyList((current) =>
        current.filter((survey) => survey.id !== index)
      );
      }
    
    const fetchEvents = () => {
        fetch("/api/schedule", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": localStorage.getItem("authToken")
          },
        })
          .then((res) => {
            if(res.status===404){
                throw new Error("NO SCHEDULE");
            }
            else if (res.ok)
              return res.json();
            else
              throw new Error("AN ISSUE CONNECTING TO BACKEND");
          }
          )
          .then(result => {
            setEventList(result);
          })
          .catch((err) => {
            console.log(err.message)
            if(err.message !== "NO SCHEDULE"){
            setError(err);
            console.log("Error:", err);
           }
           if(err.message === "NO SCHEDULE"){
            setEventList(null);
           }
          });
      };


    const fetchSurveys = () => {
        fetch("/api/survey/instructor?user=" + localStorage.getItem("loginID"), {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("authToken")
            },
        })
            .then((res) => {
                if (res.ok)
                    return res.json();
                else
                    throw new Error("AN ISSUE CONNECTING TO BACKEND");
            }
            )
            .then(result => {
                setSurveyList(result);
                setIsLoaded(true);
            })
            .catch((err) => {
                setIsLoaded(true);
                console.log("Error:", err);
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
        
        <div className={styles.surveys}>
            <div>
                <SideBar />
            </div>



            <div className="container">

                <h1 className="page-title">My Surveys</h1>

                <div className="courses-options">
                    <div className="courses-btn">
                        <button onClick={(event) => {event.preventDefault(); navigate('/createSurvey');}}> Create Survey </button>
                    </div>

                    <table className={styles.table}>
                        <thead className={styles.th}>

                        <tr className={styles.tr}>
                            <th>Survey Type</th>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Survey's Start Date</th>
                            <th>Survey's Due Date</th>
                            <th>Edit Count</th>
                            <th>Status</th>
                            <th>Edit</th>
                            <th>Preview</th>
                            <th>Completed (%)</th>
                            <th>Statistics</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody className={styles.td}>

                        {surveyList.map(survey => (
                        <SurveyTable_Instructor key={survey.id}
                            surveyID={survey.id}
                            surveyType={survey.surveyType}
                            courseCode={survey.courseCode}
                            courseName={survey.courseName}
                            startingDatetime={survey.startingDatetime}
                            deadline={survey.deadline}
                            editCount={survey.trialCount}
                            status={survey.status}
                            completedPercent={survey.completedPercent} 
                            courseID= {survey.courseId} 
                            func = {removeSurvey} 
                            canSeeStats = {survey.status==="SUBMITTED" && (survey.deadline===null ? false : (new Date(survey.deadline))<new Date())}/>
                    ))}

                        </tbody>
                    </table>


                </div>

            </div>

        </div>
        </div>
    );
}
}
export default InstructorSurveys;