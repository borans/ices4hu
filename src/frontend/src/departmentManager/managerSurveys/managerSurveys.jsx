import { useNavigate } from 'react-router-dom';
import SideBar from "../sidebarManager/sidebarManager";
import React, { useState, useEffect } from "react";
import SurveyTable_Manager from "../surveyTableManager/surveyManager";
import styles from "./dmSurveyStyle.css";
import TopBar from "../topbarManager/topbarManager";


function ManagerSurveys() {

    const [surveyList, setSurveyList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);

    let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;


    useEffect(() => {
        fetchSurveys()
    }, [])


    const handleApproveOrDeny = (newEvalReq) => {
        
        console.log(newEvalReq);

        if(newEvalReq.reevaluation.openForReevaluation){

            if(newEvalReq.reevaluation.approveClicked & !newEvalReq.reevaluation.denyClicked){
                sendApproveRequest(newEvalReq.reevaluation.surveyID);
            }else if(!newEvalReq.reevaluation.approveClicked & newEvalReq.reevaluation.denyClicked ){
                sendDenyRequest(newEvalReq.reevaluation.surveyID);
            }

        }else{

            alert("There is no re-evaluation request for this survey.");
        }


    }

    const sendApproveRequest = (Id) => {

        console.log(Id);

        fetch("/api/reevaluation_request/department_manager/approve?survey=" + String(Id) + "&user=" + localStorage.getItem("loginID"), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("authToken")
            },
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
              alert("COULD NOT APPROVE")
            });
    };



    const sendDenyRequest = (Id) => {

        fetch("/api/reevaluation_request/department_manager/deny?survey=" + String(Id) + "&user=" + localStorage.getItem("loginID"), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("authToken")
            },
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
              alert("COULD NOT DENY")
            });
    };


    const fetchSurveys = () => {
        fetch("/api/survey/department_manager?user=" + localStorage.getItem("loginID"), {
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

        <div className="withside">
            <div>
                <SideBar />
            </div>

                    <div className="container">
                        <h1 className="page-title">Surveys</h1>
                        <table className="survey-table">

                            <thead className={styles.th}>

                            <tr className={styles.tr}>
                                <th>Survey Type</th>
                                <th>Course Code</th>
                                <th>Course Name</th>
                                <th>Instructor Name</th>
                                <th>Start Time</th>
                                <th>Deadline</th>
                                <th>Reevaluation Request</th>
                                <th>Approve</th>
                                <th>Deny</th>
                                <th>Statistics</th>
                                <th>Preview</th>                                
                                <th>See Student Answers</th>
                            </tr>
                            </thead>
                            <tbody className={styles.td}>

                            {surveyList.map(survey => (
                            <SurveyTable_Manager key={survey.id}
                                surveyID={survey.id}
                                surveyType={survey.surveyType}
                                courseCode={survey.courseCode}
                                courseName={survey.courseName}
                                instructorName={survey.instructorName}
                                startingDatetime={survey.startingDatetime}
                                deadline={survey.deadline}
                                openForReevaluation={survey.openForReevaluation}
                                onSave={handleApproveOrDeny}
                                />


                        ))}

                            </tbody>
                        </table>


                    </div>


        </div>

        </div>
    );
}
}
export default ManagerSurveys;