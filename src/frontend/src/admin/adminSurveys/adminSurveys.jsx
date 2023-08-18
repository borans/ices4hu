import { useNavigate } from 'react-router-dom';
import SideBar from "../sidebarAdmin/sidebarAdmin";
import React, { useState, useEffect } from "react";
import SurveyTable_Admin from '../surveyTableAdmin/surveyAdmin';
import styles from "../../components/surveys.module.css";
import TopBar from "../topbarAdmin/topbarAdmin";
import my_surveys from "../../assets/my_surveys.png";
import folder_logo from "../../assets/folder_logo.png";

function AdminSurveys() {

    const [surveyList, setSurveyList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);

    let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;


    useEffect(() => {
        fetchSurveys()
    }, [])


    const fetchSurveys = () => {
        fetch("/api/survey/admin", {
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
                  <h1 className="page-title">Surveys</h1>
            <div>
                    <table className={styles.table}>
                        <thead className={styles.th}>
                        <tr className={styles.tr}>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Survey Type</th>
                            <th>Instructor</th>
                            <th>Creation Time</th>
                            <th>Start Time</th>
                            <th>Deadline</th>
                            <th>Edit</th>
                            <th>View</th>
                            <th>Statistics</th>
                        </tr>
                        </thead>
                        <tbody className={styles.td}>

                        {surveyList.map(survey => (
                        <SurveyTable_Admin key={survey.id}
                            courseCode={survey.courseCode}
                            surveyID={survey.id}
                            courseName={survey.courseName}
                            surveyType={survey.surveyType}
                            instructorName={survey.instructorName}
                            creationDatetime={survey.creationDatetime}
                            startingDatetime={survey.startingDatetime}
                            deadline={survey.deadline} 
                            status= {survey.status}/>
                            

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
export default AdminSurveys;