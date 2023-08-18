import SideBar from "../sidebarManager/sidebarManager";
import React, { useState, useEffect } from "react";
import "./dmSubmitters.css"
import TopBar from "../topbarManager/topbarManager";
import { useParams, useNavigate} from "react-router-dom";

function SurveySubmittersList() {
    let navigate = useNavigate();
    const { surveyID } = useParams();
    const [submittersList, setSubmittersList] = useState([]);
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    let HACKINGTRY = localStorage.getItem("loginID") === null ? true : false;


    useEffect(() => {
        fetchSurveys()
    }, [])


    const fetchSurveys = () => {
        fetch("/api/survey/department_manager/submitter_students?survey=" + surveyID+ "&user=" + localStorage.getItem("loginID"), {
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
                setSubmittersList(result);
                setIsLoaded(true);
            })
            .catch((err) => {
                setIsLoaded(true);
                setError(err);
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
                   <div>
                     <SideBar />
                   </div>
                   <div>
                     <TopBar />
                   </div>
                   <div className="container">
                     <h1 className="page-title">View Submitters of Survey</h1>
                     <div className="instructor-btn">

                     <table className="instructor-table">
                       <thead>
                         <tr>
                           <th>Name</th>
                           <th>Surname</th>
                           <th>E-mail Address</th>
                           <th>See Answers</th>
                         </tr>
                       </thead>

                            {submittersList.map((student, index) => {
                                return(                       
                            <tbody key={index}>
                            <tr>
                           <td>{student.name}</td>
                           <td>{student.surname}</td>
                           <td>{student.email}</td>
                           <td><button onClick={(event) => {event.preventDefault(); navigate(`/viewSurveyAnswers/${surveyID}/${student.loginId}`);}} className="action-button remove-button">See Answers</button></td>
                         </tr>
                       </tbody>
                                );
                            })}
                     </table>
                   </div>
                </div>
             </div>
         );
       }
        }
export default SurveySubmittersList;