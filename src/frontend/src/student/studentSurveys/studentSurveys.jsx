import { useNavigate } from 'react-router-dom';
import SideBar from '../sidebarStudent/sidebarStudent';
import React, { useState, useEffect } from "react";
import SurveyTable_Student from "../surveyTableStudent/surveyStudent";
import styles from "../../components/surveys.module.css";
import TopBar from "../topbarStudent/topbarStudent";
import my_surveys from "../../assets/my_surveys.png";
import folder_logo from "../../assets/folder_logo.png";

function StudentSurveys() {

    const [surveyList, setSurveyList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);

    let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;


    useEffect(() => {
        fetchSurveys()
    }, [])


    const fetchSurveys = () => {
        fetch("/api/survey/student?user=" + localStorage.getItem("loginID"), {
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
            <div>

                <table className={styles.table}>

                    <thead className={styles.th}>

                    <tr className={styles.tr}>
                        <th>Course Code</th>
                        <th>Course Name</th>
                        <th>Survey Type</th>
                        <th>Instructor</th>
                        <th>Start Time</th>
                        <th>Deadline</th>
                        <th>Edit Count</th>
                        <th>Edit / View</th>
                        <th>Evaluation Form Status</th>
                        <th>Remaining Days</th>
                    </tr>
                    </thead>
                    <tbody className={styles.td}>

                    {surveyList.map(survey => (
                    <SurveyTable_Student key={survey.id}
                        surveyID={survey.id}
                        surveyType={survey.surveyType}
                        courseCode={survey.courseCode}
                        courseName={survey.courseName}
                        instructorName={survey.instructorName}
                        startingDatetime={survey.startingDatetime}
                        deadline={survey.deadline}
                        editCount={survey.trialCount}
                        status={survey.status}
                        daysUntilDeadline={survey.daysUntilDeadline} />
                        

                ))}

                    </tbody>
                </table>

</div>
            </div>


        </div></div>
    );
}
}
export default StudentSurveys;